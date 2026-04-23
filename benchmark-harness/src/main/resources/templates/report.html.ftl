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
.rotate-th {
    writing-mode: vertical-rl;
    transform: rotate(180deg);
    white-space: nowrap;
    padding: 10px 5px;
    max-height: 150px;
    text-align: left;
}
</style>
</head>
<body>
<h1>1BRC Benchmark Report</h1>
<p><strong>Run Timestamp:</strong> ${timestamp}</p>
<#if sysInfo?has_content>
<div class="card" style="margin-bottom: 20px; background: #f0f8ff;">
    <h3 style="margin-top: 0;">Machine Hardware Profile</h3>
    <ul style="list-style-type: none; padding: 0; margin: 0; display: flex; gap: 20px; flex-wrap: wrap;">
        <#list sysInfo?keys as k>
        <li><strong>${k}:</strong> ${sysInfo[k]}</li>
        </#list>
    </ul>
</div>
</#if>

<div id="matrix-view">
    <div class="dashboard">
    <div class="card"><h3>Executive Anomaly Dashboard</h3><p><em>(Regression data placeholder)</em></p></div>
    </div>

    <#list datasets as ds>
    <h2>Dataset: ${ds}</h2>
    <div style="overflow-x: auto;">
        <table>
        <tr>
            <th>Environment (JDK | GC | VM Options | Taskset)</th>
            <#list classes as cls>
            <th class="rotate-th">${cls}</th>
            </#list>
        </tr>

        <#list environments as env>
        <tr>
            <td style="font-family: monospace; white-space: nowrap;">${env}</td>
            <#list classes as cls>
            <#assign k = env + " | " + ds + " | " + cls>
            <#assign rd = matrix[k]!>
            <#if rd?has_content>
            <td style="cursor: pointer; background-color: #f8fbf8; white-space: nowrap; text-align: right;" onclick="showDetails('${k?js_string}')" onmouseover="this.style.backgroundColor='#e0ffe0'" onmouseout="this.style.backgroundColor='#f8fbf8'">
                ${rd.medianRuntimeMs} ms<#if (rd.gcPauseMs > 0)><br><small style="color: #666;">GC: ${rd.gcPauseMs}ms</small></#if>
            </td>
            <#else>
            <td style="text-align: center; color: #aaa;">-</td>
            </#if>
            </#list>
        </tr>
        </#list>
        </table>
    </div>

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
const classStatuses = ${classStatusesJson!'{}'};
const baselineChecksums = ${baselineChecksumsJson!'{}'};

function showDetails(key) {
    const data = benchmarkData[key];
    if (!data) {
        alert("No data available for this run.");
        return;
    }

    document.getElementById('matrix-view').style.display = 'none';
    document.getElementById('details-view').style.display = 'block';
    
    document.getElementById('detail-title').innerText = 'Metrics: ' + key;

    const parts = key.split(" | ");
    const envDs = parts.slice(0, 6).join(" | ");
    const cls = parts[6];
    const status = classStatuses[cls] || "complete";
    const baselineChecksum = baselineChecksums[envDs];

<#noparse>
    let checksumHtml = `<code style="background:#eee;padding:2px 4px;border-radius:3px;">${data.checksum}</code>`;
    
    if (status === "baseline") {
        checksumHtml += ' <span style="color:#0056b3; font-weight:bold;">[Ground Truth]</span>';
    } else if (status === "incomplete") {
        checksumHtml += ' <span style="color:#6c757d; font-weight:bold;">[Skip: Known Incorrect]</span>';
    } else if (status === "complete") {
        if (baselineChecksum) {
            if (data.checksum === baselineChecksum) {
                checksumHtml += ' <span style="color:#28a745; font-weight:bold;">[Pass]</span>';
            } else {
                checksumHtml += ' <span style="color:#dc3545; font-weight:bold;">[Fail]</span>';
            }
        } else {
             checksumHtml += ' <span style="color:#fd7e14; font-weight:bold;">[No Baseline to compare]</span>';
        }
    }
</#noparse>

    const tbody = document.getElementById('detail-body');
<#noparse>
    // Java String.hashCode() equivalent
    let hash = 0;
    for (let i = 0; i < key.length; i++) {
        hash = ((hash << 5) - hash) + key.charCodeAt(i);
        hash |= 0;
    }
    if (hash < 0) hash = 0xFFFFFFFF + hash + 1;
    const hashHex = hash.toString(16);

    tbody.innerHTML = `
        <tr><th colspan="2" style="background:#e9ecef;text-align:center;">Runtime Profiling (3 Stages)</th></tr>
        <tr><th>Clean P50 Median Runtime</th><td><strong>${data.medianRuntimeMs} ms</strong></td></tr>
        <tr><th>Perf Stat Overhead Run</th><td>${data.perfRuntimeMs} ms <small style="color:#666;">(+${data.perfRuntimeMs - data.medianRuntimeMs} ms)</small></td></tr>
        <tr><th>JFR Agent Overhead Run</th><td>${data.jfrRuntimeMs} ms <small style="color:#666;">(+${data.jfrRuntimeMs - data.medianRuntimeMs} ms)</small></td></tr>
        <tr><th>Checksum</th><td>${checksumHtml}</td></tr>
        
        <tr><th colspan="2" style="background:#e9ecef;text-align:center;">Hardware Telemetry (Perf Stat)</th></tr>
        <tr><th>Instructions Executed</th><td>${data.instructions.toLocaleString()}</td></tr>
        <tr><th>CPU Cycles</th><td>${data.cycles.toLocaleString()}</td></tr>
        <tr><th>Instructions Per Cycle (IPC)</th><td>${data.ipc}</td></tr>
        <tr><th>Branches</th><td>${data.branches.toLocaleString()}</td></tr>
        <tr><th>Branch Misses</th><td>${data.branchMisses.toLocaleString()}</td></tr>
        <tr><th>L1 Data Cache Misses</th><td>${data.l1Misses ? data.l1Misses.toLocaleString() : '0'}</td></tr>
        <tr><th>Last Level Cache Misses</th><td>${data.llcMisses ? data.llcMisses.toLocaleString() : '0'}</td></tr>
        <tr><th>Page Faults</th><td>${data.pageFaults ? data.pageFaults.toLocaleString() : '0'}</td></tr>
        <tr><th>Context Switches</th><td>${data.contextSwitches ? data.contextSwitches.toLocaleString() : '0'}</td></tr>
        <tr><th>CPU Migrations</th><td>${data.cpuMigrations ? data.cpuMigrations.toLocaleString() : '0'}</td></tr>
        
        <tr><th colspan="2" style="background:#e9ecef;text-align:center;">JVM Profiling (Java Flight Recorder)</th></tr>
        <tr><th>GC Pause Time</th><td>${data.gcPauseMs} ms</td></tr>
        <tr><th>Allocated Bytes</th><td>${data.allocatedBytes.toLocaleString()} bytes</td></tr>
        <tr><th>JIT Compilation Time</th><td>${data.jitCompilationMs} ms</td></tr>
        
        <tr><td colspan="2" style="text-align: center; padding-top: 20px;">
            <a href="permutations/history-${hashHex}.html" class="btn" style="background: #28a745;">&#128200; View Historical Trend for this Test</a>
        </td></tr>
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
