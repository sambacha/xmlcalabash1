<p:declare-step xmlns:t="http://xproc.org/ns/testsuite" xmlns:p="http://www.w3.org/ns/xproc" xmlns:c="http://www.w3.org/ns/xproc-step" xmlns:err="http://www.w3.org/ns/xproc-error" version="1.0">
      <p:input port="source" sequence="true"/>
      <p:output port="result"/>
      
      <p:for-each>
        <p:variable name="p1" select="p:iteration-position()"/>
        <p:variable name="s1" select="p:iteration-size()"/>

        <!-- a p:group wrapper to tests that p:iteration-position
             and p:iteration-size are not affected -->
        <p:group>
          <p:variable name="p2" select="p:iteration-position()"/>
          <p:variable name="s2" select="p:iteration-size()"/>

          <p:for-each>
            <p:iteration-source select="/doc/para"/>
            <p:variable name="p3" select="p:iteration-position()"/>
            <p:variable name="s3" select="p:iteration-size()"/>
            
            <p:add-attribute match="item" attribute-name="pos">
              <p:input port="source">
                <p:inline>
                  <item/>
                </p:inline>
              </p:input>
              <p:with-option name="attribute-value" select="concat($p1, ',', $s1, '-', $p2, ',', $s2, '-', $p3, ',', $s3)"/>
            </p:add-attribute>
            
          </p:for-each>
        </p:group>
      </p:for-each>

      <p:wrap-sequence wrapper="result"/>

    </p:declare-step>