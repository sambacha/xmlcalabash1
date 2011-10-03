<p:declare-step xmlns:foo="http://acme.com/test" xmlns:t="http://xproc.org/ns/testsuite" xmlns:p="http://www.w3.org/ns/xproc" xmlns:c="http://www.w3.org/ns/xproc-step" xmlns:err="http://www.w3.org/ns/xproc-error" version="1.0">
      <p:output port="result"/>

      <p:declare-step type="foo:test1">
        <p:output port="result"/>
        
        <p:declare-step type="foo:nested">
          <p:output port="result"/>
          <p:identity>
            <p:input port="source">
              <p:inline><doc1/></p:inline>
            </p:input>
          </p:identity>
        </p:declare-step>

        <foo:nested/>
      </p:declare-step>

      <p:declare-step type="foo:test2">
        <p:output port="result"/>

        <p:declare-step type="foo:nested">
          <p:output port="result"/>
          <p:identity>
            <p:input port="source">
              <p:inline><doc2/></p:inline>
            </p:input>
          </p:identity>
        </p:declare-step>

        <foo:nested/>
      </p:declare-step>


      <foo:test1 name="test1"/>
      <foo:test2 name="test2"/>

      <p:wrap-sequence wrapper="wrapper">
        <p:input port="source">
          <p:pipe step="test1" port="result"/>
          <p:pipe step="test2" port="result"/>
        </p:input>
      </p:wrap-sequence>
  
    </p:declare-step>