<p:declare-step xmlns:t="http://xproc.org/ns/testsuite" xmlns:p="http://www.w3.org/ns/xproc" xmlns:c="http://www.w3.org/ns/xproc-step" xmlns:err="http://www.w3.org/ns/xproc-error" version="1.0" name="main">
<p:output port="result"/>

<p:documentation><para xmlns="http://docbook.org/ns/docbook">This
is just documentation, it will be ignored by the processor.</para>
</p:documentation>

<p:pipeinfo xml:id="foo">
<config>This is for the processor</config>
</p:pipeinfo>

<p:identity name="identity">
<p:input port="source">
<p:document href="#foo"/>
</p:input>
</p:identity>

</p:declare-step>