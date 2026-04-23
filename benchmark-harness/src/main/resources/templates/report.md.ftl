# 1BRC Benchmark Report

**Run Timestamp:** ${timestamp}

<#list datasets as ds>
## Dataset: ${ds}

| Class |<#list environments as env> ${env} |</#list>
|<#list 0..environments?size as i>---|</#list>
<#list classes as cls>
| ${cls} |<#list environments as env><#assign k = env + " | " + ds + " | " + cls><#assign rd = matrix[k]!><#if rd?has_content> ${rd.medianRuntimeMs} ms |<#else> - |</#if></#list>
</#list>

</#list>
