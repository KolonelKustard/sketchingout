option explicit

dim xml

set xml = CreateObject("Microsoft.XMLDOM")
xml.async = false
xml.validateOnParse = true
xml.load("consequences.xml")

MsgBox "Error: " & xml.parseError.reason & " (" & xml.parseError.line & ")"