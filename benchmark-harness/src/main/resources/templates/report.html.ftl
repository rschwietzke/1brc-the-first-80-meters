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
<td>${rd.medianRuntimeMs} ms<#if (rd.gcPauseMs > 0)><br><small>GC: ${rd.gcPauseMs}ms</small></#if></td>
<#else>
<td>-</td>
</#if>
</#list>
</tr>
</#list>
</table>

<div id="chart-${ds?index}" style="width: 100%; height: 400px; margin-bottom: 50px;"></div>
<script>
var myChart = echarts.init(document.getElementById('chart-${ds?index}'));
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
</script>
</#list>

</body>
</html>
