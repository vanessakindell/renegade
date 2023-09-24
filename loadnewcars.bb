AppTitle "Renegade add-on car script builder","Quiting will result in a broken file!"


Text 0,0,"Loading..."
loadaddoncars("newadd.txt")
If FileType("addoncars.txt")=1 Then
	loadaddoncars("addoncars.txt")
EndIf
Cls
Text 0,0,"Saving..."
saveaddon()
DeleteFile("newadd.txt")
End

Type cmodel
	Field number,ID,loadname$,wheel,whoop,flightK
End Type

Type scriptentry
	Field sdat$
End Type

Function LoadAddonCars(file$)

If FileType(file$)=1

	fhandle=ReadFile(file$)
	If fhandle<>1
		While Eof(fhandle)=0
			cmd$=ReadLine$(fhandle)
			If Left$(cmd$,1)<>";" And Len(cmd$)<>0
				parse.scriptentry=New scriptentry
				parse\sdat=cmd$
			EndIf
		Wend
		CloseFile fhandle

		parse.scriptentry=First scriptentry
		While parse<>Null
			If Left$(parse\sdat,1)="@"
			cmd$=Mid$(parse\sdat,2,Len(parse\sdat)-2)
				Select cmd$
;					Case "cars"
;					parse=After parse
;					Repeat
;						m.cmodel=New cmodel
;						sep=Instr(parse\sdat,"=")
;						del1$=Mid$(parse\sdat,6,sep-6)
;						m\number=del1$
;						m\loadname$=Right$(parse\sdat,Len(parse\sdat)-sep)
;						parse=After parse
;					Until parse\sdat=")"	
					
												
					Case "cars"

					parse=After parse
					Repeat
						w.cmodel=New cmodel
						cpos=1

						Repeat
							sep1=Instr(parse\sdat,"=",cpos)
							del1$=Mid$(parse\sdat,cpos,sep1-cpos)
							sep2=Instr(parse\sdat,",",cpos)
							del2$=Mid$(parse\sdat,sep1+1,sep2-sep1-1)

							Select del1$
							Case "name"
								w\loadname$=del2$

							Case "wheel"
								w\wheel=del2
							
							Case "laser"
								w\whoop=del2
							
							Case "flight"
								w\flightK=del2

							End Select
							cpos=sep2+1
						Until sep1=0 Or sep2=0
						parse=After parse
					Until parse\sdat=")"	

				End Select
			EndIf
			parse=After parse
		Wend
	Else
		RuntimeError"Script File "+Chr$(34)+file$+Chr$(34)+" is not readable."
	EndIf

Else
	RuntimeError"Script File "+Chr$(34)+file$+Chr$(34)+" is not a file."
EndIf	

For s.scriptentry = Each scriptentry
	Delete s
Next

End Function

Function saveaddon()
		file=WriteFile("addoncars.txt")
		WriteLine file,"@cars("
			For c.cmodel=Each cmodel
				WriteLine file, "name=" + c\loadname$ + ",wheel=" + c\wheel + ",laser=" + c\whoop + ",flight=" + c\flightK
			Next
		WriteLine file,")"
		CloseFile file
End Function
				
				
				
				
				
				
				
				
				
				
		