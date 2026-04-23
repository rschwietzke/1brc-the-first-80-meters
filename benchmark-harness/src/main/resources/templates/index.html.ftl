<!DOCTYPE html>
<html>
<head>
<title>1BRC Historical Overview</title>
<script src="https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js"></script>
<style>
body { font-family: system-ui, sans-serif; margin: 20px; background: #f9f9f9; }
table { border-collapse: collapse; width: 100%; margin-bottom: 30px; background: white; }
th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
th { background-color: #f2f2f2; }
tr:hover { background-color: #f5f5f5; }
.card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); margin-bottom: 30px; }
.comment { color: #666; font-style: italic; }
.btn { display: inline-block; padding: 6px 12px; background: #007bff; color: white; text-decoration: none; border-radius: 4px; font-size: 0.9em; }
.btn:hover { background: #0056b3; }
.fast { color: #28a745; font-weight: bold; }
</style>
</head>
<body>
<h1>1BRC Historical Overview Dashboard</h1>
<p>Generated: ${generatedAt}</p>

<div class="card">
    <h2>Performance Trend (Fastest Median Runtime)</h2>
    <div id="trend-chart" style="width: 100%; height: 400px;"></div>
</div>

<div class="card">
    <h2>Execution History</h2>
    <table>
        <thead>
            <tr>
                <th>Timestamp</th>
                <th>Comment / Objective</th>
                <th>Matrix Size</th>
                <th>Champion Class</th>
                <th>Median Runtime</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <#list summaries as run>
            <tr>
                <td><strong>${run.timestamp}</strong></td>
                <td class="comment"><#if run.comment?has_content>${run.comment}<#else>-</#if></td>
                <td>${run.totalCombinations} runs</td>
                <td>${run.fastestClass}</td>
                <td class="fast"><#if (run.fastestMedianMs > 0)>${run.fastestMedianMs} ms<#else>-</#if></td>
                <td><a href="${run.timestamp}.html" class="btn">View Details</a></td>
            </tr>
            </#list>
            <#if summaries?size == 0>
            <tr><td colspan="6">No historical runs found.</td></tr>
            </#if>
        </tbody>
    </table>
</div>

<script>
(function() {
    var chartDom = document.getElementById('trend-chart');
    if (chartDom) {
        var myChart = echarts.init(chartDom);
        var option = {
            tooltip: { trigger: 'axis' },
            xAxis: { 
                type: 'category', 
                data: [
                    <#list chronological as run>
                    '${run.timestamp}',
                    </#list>
                ] 
            },
            yAxis: { type: 'value', name: 'Median Runtime (ms)' },
            series: [{
                name: 'Fastest Median Runtime',
                type: 'line',
                smooth: true,
                areaStyle: {},
                data: [
                    <#list chronological as run>
                    ${run.fastestMedianMs},
                    </#list>
                ]
            }]
        };
        myChart.setOption(option);
    }
})();
</script>

</body>
</html>
