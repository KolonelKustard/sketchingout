option explicit

dim xml

set xml = CreateObject("Microsoft.XMLDOM")
xml.async = false
xml.validateOnParse = true

function ValidateXMLDoc(xmlDoc)
  xml.load(xmlDoc)

  if (xml.parseError.errorCode <> 0) then
    call Err.Raise( _
      xml.parseError.errorCode, _
      xmlDoc, _
      "(Line: " & xml.parseError.line & ") " & xml.parseError.reason _
    )
      
    ValidateXMLDoc = false
  else
    ValidateXMLDoc = true
  end if
end function


ValidateXMLDoc("drawing.xml")
ValidateXMLDoc("request_next.xml")
ValidateXMLDoc("response_next.xml")
ValidateXMLDoc("request_user.xml")
ValidateXMLDoc("response_user.xml")
ValidateXMLDoc("request_user_submit.xml")
ValidateXMLDoc("request_submit_drawing.xml")
ValidateXMLDoc("request_drawing.xml")
ValidateXMLDoc("response_drawing.xml")

MsgBox("No errors in XML documents")