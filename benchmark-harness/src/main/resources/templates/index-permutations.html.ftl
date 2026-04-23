<!DOCTYPE html>
<html>
<head>
<title>Benchmark Permutations Directory</title>
<style>
body { font-family: system-ui, sans-serif; margin: 20px; background: #f9f9f9; color: #333; }
.card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); margin-bottom: 30px; }
h1 { margin-bottom: 5px; }
h2 { color: #0056b3; border-bottom: 1px solid #eee; padding-bottom: 10px; margin-top: 30px; }
ul { list-style-type: none; padding-left: 0; }
li { padding: 8px 0; border-bottom: 1px solid #f2f2f2; }
li a { color: #007bff; text-decoration: none; font-family: monospace; word-break: break-all; }
li a:hover { text-decoration: underline; color: #0056b3; }
.btn { display: inline-block; padding: 8px 16px; background: #6c757d; color: white; text-decoration: none; border-radius: 4px; margin-bottom: 20px; }
.btn:hover { background: #5a6268; }
</style>
</head>
<body>

<a href="../index.html" class="btn">&larr; Back to Global Overview Dashboard</a>

<h1>Benchmark Permutations Directory</h1>
<p>Select a specific configuration permutation to view its detailed historical charts and data. Generated: ${generatedAt}</p>

<div class="card">
    <ul>
    <#list classes as className>
        <li><a href="index-${className}.html" style="font-size: 1.2em; font-family: sans-serif;">${className}</a></li>
    </#list>
    </ul>
    
    <#if groupedPermutations?size == 0>
        <p>No historical permutations found.</p>
    </#if>
</div>

</body>
</html>
