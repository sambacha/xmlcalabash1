<p:declare-step xmlns:foo="http://acme.com/test" xmlns:t="http://xproc.org/ns/testsuite" xmlns:p="http://www.w3.org/ns/xproc" xmlns:c="http://www.w3.org/ns/xproc-step" xmlns:err="http://www.w3.org/ns/xproc-error" version="1.0">
      <p:input port="source"/>
      <p:output port="result"/>

      <p:declare-step type="foo:test">
	<p:input port="source"/>
	<p:output port="result"/>
        <p:count/>
      </p:declare-step>

      <foo:test/>
    </p:declare-step>