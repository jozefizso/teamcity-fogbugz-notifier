<%@ include file="/include-internal.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<tr>
  <td colspan="2">
    <em>Publish build status to FogBugz with Extended Notifications plugin.</em>
  </td>
</tr>

<tr>
  <th><label for="fbAddress">FogBugz Address:<l:star/></label></th>
  <td>
    <props:textProperty name="fbAddress" className="longField"/>
    <span class="smallNote">Specify address to the FogBugz Extended Notifications plugin endpoint.</span>
    <span class="error" id="error_fbAddress"></span>
  </td>
</tr>

<tr>
  <td colspan="2">FogBugz must have Extended Notifications plugin installed.</td>
</tr>

