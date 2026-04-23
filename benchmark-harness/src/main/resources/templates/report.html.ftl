<!DOCTYPE html>
<html>
<head>
<title>1BRC Benchmark Report - ${timestamp}</title>
<script src="https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js"></script>
<style>
body { font-family: system-ui, sans-serif; margin: 20px; background: #f9f9f9; }
table { border-collapse: collapse; width: 100%; margin-bottom: 30px; background: white; }
th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
th { background-color: #f2f2f2; }
.dashboard { display: flex; gap: 20px; margin-bottom: 30px; }
.card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); flex: 1; }
.badge { font-size: 0.8em; padding: 2px 6px; border-radius: 10px; margin-left: 8px; }
.badge-baseline { background: #ffd700; color: #000; }
.badge-incomplete { background: #ffcccc; color: #900; }
</style>
</head>
<body>
<h1>1BRC Benchmark Report</h1>
<p><strong>Run Timestamp:</strong> ${timestamp}</p>

<div id="matrix-view">
    <div class="dashboard">
    <div class="card"><h3>Executive Anomaly Dashboard</h3><p><em>(Regression data placeholder)</em></p></div>
    </div>

    <#list datasets as ds>
    <h2>Dataset: ${ds}</h2>
    <table>
    <tr><th>Class</th>
    <#list environments as env>
    <th>${env}</th>
    </#list>
    </tr>

    <#list classes as cls>
    <tr><td>${cls}</td>
    <#list environments as env>
    <#assign k = env + " | " + ds + " | " + cls>
    <#assign rd = matrix[k]!>
    <#if rd?has_content>
    <td style="cursor: pointer; background-color: #f8fbf8;" onclick="showDetails('${k?js_string}')" onmouseover="this.style.backgroundColor='#e0ffe0'" onmouseout="this.style.backgroundColor='#f8fbf8'">
        ${rd.medianRuntimeMs} ms<#if (rd.gcPauseMs > 0)><br><small>GC: ${rd.gcPauseMs}ms</small></#if>
    </td>
    <#else>
    <td>-</td>
    </#if>
    </#list>
    </tr>
    </#list>
    </table>

    <div id="chart-${ds?index}" style="width: 100%; height: 400px; margin-bottom: 50px;"></div>
    <script>
    (function() {
        var chartDom = document.getElementById('chart-${ds?index}');
        if (chartDom) {
            var myChart = echarts.init(chartDom);
            var option = { title: { text: '${ds}' }, tooltip: {}, xAxis: { data: [
            <#list classes as cls>'${cls}',</#list>
            ] }, yAxis: {}, series: [
            <#list environments as env>
            { name: '${env}', type: 'bar', data: [
            <#list classes as cls>
            <#assign k = env + " | " + ds + " | " + cls>
            <#assign rd = matrix[k]!>
            <#if rd?has_content>${rd.medianRuntimeMs}<#else>0</#if>,
            </#list>
            ] },
            </#list>
            ] };
            myChart.setOption(option);
        }
    })();
    </script>
    </#list>
</div>

<div id="details-view" style="display: none;">
    <button onclick="hideDetails()" style="padding: 10px 20px; cursor: pointer; margin-bottom: 20px; font-weight: bold;">&larr; Back to Matrix Overview</button>
    <div class="card" style="margin-bottom: 30px;">
        <h2 id="detail-title">Run Details</h2>
        <table id="detail-table" style="width: 50%; max-width: 600px;">
            <tbody id="detail-body"></tbody>
        </table>
    </div>
</div>

<script>
const benchmarkData = ${jsonData!'{}'};

function showDetails(key) {
    const data = benchmarkData[key];
    if (!data) {
        alert("No data available for this run.");
        return;
    }

    document.getElementById('matrix-view').style.display = 'none';
    document.getElementById('details-view').style.display = 'block';
    
    document.getElementById('detail-title').innerText = 'Metrics: ' + key;

    const tbody = document.getElementById('detail-body');
<#noparse>
    tbody.innerHTML = `
        <tr><th>Median Runtime</th><td><strong>${data.medianRuntimeMs} ms</strong></td></tr>
        <tr><th>Instructions Executed</th><td>${data.instructions.toLocaleString()}</td></tr>
        <tr><th>CPU Cycles</th><td>${data.cycles.toLocaleString()}</td></tr>
        <tr><th>Branches</th><td>${data.branches.toLocaleString()}</td></tr>
        <tr><th>Branch Misses</th><td>${data.branchMisses.toLocaleString()}</td></tr>
        <tr><th>Instructions Per Cycle (IPC)</th><td>${data.ipc}</td></tr>
        <tr><th>GC Pause Time</th><td>${data.gcPauseMs} ms</td></tr>
        <tr><th>Allocated Bytes</th><td>${data.allocatedBytes.toLocaleString()} bytes</td></tr>
        <tr><th>JIT Compilation Time</th><td>${data.jitCompilationMs} ms</td></tr>
    `;
</#noparse>
    window.scrollTo(0, 0);
}

function hideDetails() {
    document.getElementById('details-view').style.display = 'none';
    document.getElementById('matrix-view').style.display = 'block';
}
</script>

</body>
</html>
