<p:declare-step xmlns:t="http://xproc.org/ns/testsuite" xmlns:p="http://www.w3.org/ns/xproc" xmlns:c="http://www.w3.org/ns/xproc-step" xmlns:err="http://www.w3.org/ns/xproc-error" version="1.0">
      <p:http-request>
        <p:input port="source">
          <p:inline>
            <c:request method="POST" href="http://tests.xproc.org/service/check-multipart">
              <c:multipart boundary="--carrot" content-type="multipart/mixed">
                <c:body content-type="text/plain">foo</c:body>
              </c:multipart>
            </c:request>
          </p:inline>
        </p:input>
      </p:http-request>
      <p:sink/>
    </p:declare-step>