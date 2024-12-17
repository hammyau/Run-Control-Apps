

digraph xml {
rankdir=LR

<#macro vdpcolour type>
<#switch type>
  <#case "int">
    "palegreen"
    <#break>
  <#case "short">
   "skyblue"
    <#break>
  <#case "byte">
   "orange"
    <#break>
  <#case "enum">
   "yellow"
    <#break>
  <#case "boolean">
   "beige"
    <#break>
  <#default>
    "pink"
</#switch>
</#macro>

<#macro compcolour type>
<#switch type>
  <#case "int">
    "palegreen"
    <#break>
  <#case "short">
   "skyblue"
    <#break>
  <#case "byte">
   "orange"
    <#break>
  <#case "enum">
   "yellow"
    <#break>
  <#case "boolean">
   "beige"
    <#break>
  <#case "String">
   "pink"
    <#break>
  <#default>
    "yellow"
</#switch>
</#macro>


graph [label="VDP Records to Component\n", labelloc=t, labeljust=center, fontname=Helvetica, fontsize=22];
labeljust=center; ranksep = "3 equally"

<#assign comps = wm.segments["Components"] /> 

<#list comps.components as k>
    
subgraph cluster_${k.name} { label="${k.name}" node [shape=plaintext]
        
${k.name} [label=<<TABLE BORDER="0" CELLBORDER="1" CELLSPACING="0">
    <TR>
        <TD ALIGN="LEFT" BGCOLOR="white">Name</TD>
        <TD ALIGN="LEFT" BGCOLOR="white">Type</TD>
    </TR>
    <#list k.component.members as mm>
    <TR>
        <TD ALIGN="LEFT" BGCOLOR=<@compcolour mm.type!"missing"/>  PORT="${mm.name}" >${mm.name}</TD>
        <TD ALIGN="LEFT" BGCOLOR=<@compcolour mm.type!"missing"/>  >${mm.type!"missing"}</TD>
    </TR>
    </#list>
    </TABLE>>]
}
</#list> 

<#assign vdprs = wm.segments["VDPRecords"] />
<#list vdprs.records as r>
<#assign record = r["record"] />
<#if record.componentName != "none">
subgraph cluster_${record.recordName} { label="${record.recordName}" node [shape=plaintext]
        
${record.recordName} [label=<<TABLE BORDER="0" CELLBORDER="1" CELLSPACING="0">
    <TR>
        <TD ALIGN="LEFT" BGCOLOR="white">Name</TD>
        <TD ALIGN="LEFT" BGCOLOR="white">Type</TD>
    </TR>
    <#list record.fields as ff>
        <TR>
        <#if ff.componentField != "none">
            <TD ALIGN="LEFT" BGCOLOR=<@vdpcolour ff.type!"missing"/>  >${ff.name}</TD>
            <TD ALIGN="LEFT" BGCOLOR=<@vdpcolour ff.type!"missing"/>  PORT="${ff.name?replace(".","PP")}" >${ff.type!"missing"}</TD>
        <#else>
            <TD ALIGN="LEFT" BGCOLOR="lightgrey"  >${ff.name}</TD>
            <TD ALIGN="LEFT" BGCOLOR="lightgrey"  PORT="${ff.name?replace(".","PP")}" >${ff.type!"missing"}</TD>
        </#if>
        </TR>
    </#list>
    </TABLE>>]
}
    <#list record.fields as rr>
        <#if rr.componentField != "none">
            ${record.recordName}:${rr.name?replace(".","PP")} -> ${record.componentName}:${rr.componentField} [dir="both"]
        </#if>
    </#list>
</#if>
</#list>
}
