; Renegade By Sonic Waves TM
; Source coded by Sonic Waves TM
; Cleared for public release
; Last modified 08/24/2010

AppTitle "Renegade v1.3","Ending this way won't save. Use ESC! Continue?"

Include "launcher3d.bb"

;Include "CDKeyTest.bb"

ChangeDir "data"

m1#=1
m2#=3
m3#=5
m4#=2

;inputkey(0)

If FileType("newadd.txt")=1 And iAdditional=1 Then
	ExecFile("loadnewcars.exe")
	Text 0,0,"Adding new cars to game..."
	Text 0,FontHeight(),"Press any key when done to continue..."
	FlushKeys()
	WaitKey()
EndIf

Global timer=CreateTimer(60)

;setup system stuff
SetBuffer(BackBuffer())
HidePointer()

;load game
Global name$=MilliSecs()
Global filename$="gamesave"
Global gametype#=1
Global mode#=2
Global control#=1
Global consoleout=False
Global maxtrax#=0
Global traxlist$="trax.txt"
HidePointer()

If FileType("game.inf")=1 Then

	information=ReadFile("game.inf")
	name$=ReadLine(information)
	filename$=ReadLine(information)
	mode#=ReadLine(information)
	control#=ReadLine(information)
	traxlist$=ReadLine(information)
	CloseFile information
EndIf

buildtracklist()

;play video
Global Intro=OpenMovie("Intro.mpg")
If Intro=0 Then
info("Error - Movie Not Loaded")
savelog()
RuntimeError "Error - Movie Not Loaded"
EndIf
If Not MoviePlaying(Intro) Then
info("Error - Movie Not Playing")
savelog()
RuntimeError "Error - Movie Not Playing"
EndIf

Global Play=True

While play=True
If  MoviePlaying(Intro)=True Then play=True
Cls
DrawMovie Intro,0,0,GraphicsWidth(),GraphicsHeight()
If MoviePlaying(Intro)=False Then play=False
If KeyHit(1) Then play=False
Flip
Wend

CloseMovie(Intro)


	If FileType("addoncars.txt")=1 Then
		LoadAddonCars()
	EndIf
	

;setup random
SeedRnd(MilliSecs())

;setup gfx stuff
SetBuffer(BackBuffer())

;Load GFX
Global gfxbackground=LoadImage("background.bmp")
Global gfxpoint=LoadAnimImage("point.bmp",32,32,0,4)
Global gfxoneplayer=LoadImage("oneplayer.bmp")
Global gfxlogo=LoadImage("logo.bmp")
Global gfxoptions=LoadImage("options.bmp")
Global gfxoptionsback=LoadImage("optionsback.bmp")
Global gfxcharback=LoadImage("charback.bmp")
Global gfxscar=LoadImage("Scar.bmp")
Global gfxrally=LoadImage("Rally.bmp")
Global gfxarrow=LoadImage("arrow.bmp")
Global damage=LoadAnimImage("Damage.bmp",100,50,0,11)
Global gfxarrowb=LoadImage("arrowb.bmp")

;define global variables
Global turnposit#=0
Global lstturn#=0
Global goon=False
Global falldam=1
Global laser=True
Global flight=False
Global A=(FontHeight()*0)+GraphicsHeight()
Global B=(FontHeight()*2)+GraphicsHeight()
Global BB=(FontHeight()*3)+GraphicsHeight()
Global C=(FontHeight()*5)+GraphicsHeight()
Global D=(FontHeight()*7)+GraphicsHeight()
Global E=(FontHeight()*9)+GraphicsHeight()
Global F=(FontHeight()*11)+GraphicsHeight()
Global G=(FontHeight()*13)+GraphicsHeight()
Global H=(FontHeight()*15)+GraphicsHeight()
Global I=(FontHeight()*17)+GraphicsHeight()
Global J=(FontHeight()*20)+GraphicsHeight()
Global hits#=0
Global bgm
Global gravity#=-.06
Global pointrot#=0
Global wheels[4]
Global copy[200]
Global car
Global city
Global CarToneA#=255
Global CarToneB#=255
Global CarToneC#=255
Global track#=0
Global carAT
Global camera
Global geer#
Global armed=True
Global HangTime
Global LastHang=0
Global reload#=0
Global Espeed#=0
Global speed#=0
Global Accel#=0
Global TurnFactor#
Global MaxSpeed#
Global carname$
Global Sound
Global gamehosted=0
Global x_vel#=0
Global y_vel#=0
Global z_vel#=0
Global respawncop=False
Global god=0
Global light

; Load SFX
Global engine=LoadSound("sfx\Drive.wav")
Global engine3d=Load3DSound("sfx\Drive.wav")
Global sfxcrash=Load3DSound("sfx\crash.wav")
Global sfxBeep=Load3DSound("sfx\BEEP.wav")
Global sfxShoot=Load3DSound("sfx\shoot.wav")
Global sfxAHHHH=Load3DSound("sfx\AHHHH.WAV")
Global TDsfxAHHHH=LoadSound("sfx\AHHHH.WAV")
Global sfxSpawn=Load3DSound("sfx\OPTIMIS.WAV")
Global sfxStart=LoadSound("sfx\INTRO9.WAV")
Global sfxSUCTION=Load3DSound("sfx\SUCTION.WAV")
Global sfxBOOM=Load3DSound("sfx\WORKS.WAV")
Global sfxSiren=Load3DSound("Sfx\SIREN.WAV")
Global sfxMODULAT=Load3DSound("sfx\MODULAT3.WAV")

Global CHNSFX

	Global bull_sprite=LoadSprite( "BLUSPARK.BMP" )
	ScaleSprite bull_sprite,3,3
	EntityRadius bull_sprite,1.5
	EntityType bull_sprite,TYPE_BULLET
	HideEntity bull_sprite
	
	Global spark_sprite=LoadSprite( "Bigspark.BMP" )
	EntityBlend spark_sprite,2
	SpriteViewMode spark_sprite,2
	HideEntity spark_sprite

;setup types
Type Player
	Field CarO,name$,Net_ID,car$,msg$,msgdelay#
End Type

Type track
	Field number#,loadname$
End Type

Type scriptentry
	Field sdat$
End Type

Type Spark
	Field alpha#,sprite
End Type

Type Bullet
	Field rot#,sprite,time_out
End Type

Type Cop
	Field COPBODY,CHANNEL
End Type

Type UFO
	Field COPBODY,CHANNEL,Reload
End Type

Type Bot
	Field bBODY,CHANNEL
End Type

Type Info
	Field txt$
End Type

Type npc
	Field mesh,CHANNEL
End Type

Type cmodel
	Field number,ID,loadname$,wheel,whoop,flightK
End Type

;setup constants
Const BODY=1,WHEEL=2,SCENE=3,TYPE_BULLET=4,POLICE=5,CAMBOX=6,TYPE_GRASS=7,NET=8,NET_BULLET=9,GHOST=10,TYPE_BOT=11,TYPE_ENEMYBULL=12

;load main menu
mainmenu()

;main menu
Function mainmenu()

StopChannel(bgm)

Local logoframe#=0

MoveMouse 269,179

FlushMouse()
FlushKeys()

Font=LoadFont("Microsoft Sans Serif",28,0,0,0)
SetFont Font

game=True

gfxpoint=LoadAnimImage("point.bmp",32,32,0,4)
gfxlogo=LoadImage("logo.bmp")
gfxbackground=LoadImage("background.bmp")

ResizeImage gfxbackground,GraphicsWidth(),GraphicsHeight()
MidHandle(gfxlogo)

While game=True
Cls

If KeyHit(65) Then
	scrshot()
EndIf

	If Not ChannelPlaying(bgm) Then
		bgm=PlayMusic("main.wma")
	EndIf
	
DrawImage gfxbackground,0,0

Text 650,0,"Time: "+CurrentTime$()

If KeyHit(1) Then 
writesettings()
savelog()
End
EndIf

DrawImage gfxlogo,GraphicsWidth()/2,GraphicsHeight()/4

Color 225,0,0
Text GraphicsWidth()/2,(GraphicsHeight()/2)-FontHeight()*1,"Play Game",True
Text GraphicsWidth()/2,(GraphicsHeight()/2),"Netplay",True
Text GraphicsWidth()/2,(GraphicsHeight()/2)+FontHeight()*1,"Options",True
If Not First info=Null Then
	inf.info=First info
	Text 0,0,"Info: "+inf\txt$
EndIf

DrawImage gfxpoint,MouseX(),MouseY(),pointrot#
pointrot#=pointrot#+1
If pointrot#=4 Then pointrot#=0

If MouseHit(1) Then
If MouseX()>GraphicsWidth()/2-((StringWidth("Play Game")*FontWidth())/2) And MouseX()<GraphicsWidth()/2+((StringWidth("Play Game")*FontWidth())/2) And MouseY()>(GraphicsHeight()/2)-FontHeight()*1 And MouseY()<((GraphicsHeight()/2)-FontHeight()*1)+StringHeight("Play Game") Then
oneplayerselect("Off")
EndIf
If MouseX()>GraphicsWidth()/2-((StringWidth("Netplay")*FontWidth())/2) And MouseX()<GraphicsWidth()/2+((StringWidth("Play Game")*FontWidth())/2) And MouseY()>(GraphicsHeight()/2) And MouseY()<((GraphicsHeight()/2))+StringHeight("Netplay") Then
oneplayerselect("On")
EndIf
If MouseX()>GraphicsWidth()/2-((StringWidth("Options")*FontWidth())/2) And MouseX()<GraphicsWidth()/2+((StringWidth("Options")*FontWidth())/2) And MouseY()>(GraphicsHeight()/2)+FontHeight()*1 And MouseY()<((GraphicsHeight()/2)+FontHeight()*1)+StringHeight("Options") Then
options()
EndIf
EndIf

If KeyHit(1) Then 
writesettings()
savelog()
End
EndIf
Flip

Wend

End Function



;-------------------------- ONE PLAYER SELECT FUNCTION IS HERE --------------------------


Function oneplayerselect(netstatus$)

Flight=False

StopChannel(bgm)
	
	chardisplay#=gametype#
	
	MoveMouse 0,0
	
	Local copnumber#
	
	Font=LoadFont("Microsoft Sans Serif",25,1,0,0)
	SetFont Font
	Color 255,0,0
	
	FlushMouse()
	FlushKeys()

	chardisplay#=0
	
	ResizeImage gfxbackground,GraphicsWidth(),GraphicsHeight()
	ResizeImage gfxcharback,GraphicsWidth(),GraphicsHeight()
	
	Repeat
		

If KeyHit(65) Then
	scrshot()
EndIf

	If Not ChannelPlaying(bgm) Then
		bgm=PlayMusic("select.wma")
	EndIf
	
		Cls
		
		If CarToneA#>255 Then CarToneA#=255
		If CarToneA#<0 Then CarToneA#=0
		
		If CarToneB#>255 Then CarToneB#=255
		If CarToneB#<0 Then CarToneB#=0
		
		If CarToneC#>255 Then CarToneC#=255
		If CarToneC#<0 Then CarToneC#=0
		
		DrawImage gfxbackground,0,0
		DrawImage gfxcharback,0,0
		
		
		;Text 700,0,"X: "+MouseX()
		;Text 700,20,"Y: "+MouseY()
		Text 0,0,"Time: "+CurrentTime$()
		
		DrawImage gfxarrow,475,525
		DrawImage gfxarrow,495,525
		DrawImage gfxarrow,515,525
		DrawImage gfxarrowb,475,540
		DrawImage gfxarrowb,495,540
		DrawImage gfxarrowb,515,540

		Text 40,60+FontHeight()*((-1)+MouseZ()),"[Endurance Mode: "+Respawncop+"]"
		Text 40,60+FontHeight()*(0+MouseZ()),"Sports Car"
		Text 40,60+FontHeight()*(1+MouseZ()),"Pickup Truck"
		Text 40,60+FontHeight()*(2+MouseZ()),"Convertable"
		Text 40,60+FontHeight()*(3+MouseZ()),"[Load "+filename$+"]"
		Text 40,60+FontHeight()*(4+MouseZ()),"[Use Other File]"
		Text 40,60+FontHeight()*(5+MouseZ()),"[Net Game: "+netstatus$+"]"
		Text 40,60+FontHeight()*(6+MouseZ()),"[Enimies: "+Str(Int(copnumber))+" ]"
		Text 40,60+FontHeight()*(7+MouseZ()),"[Control: "+controls$+"]"
		Text 40,60+FontHeight()*13,"----------------------------  [OK]"
		
		numbcnt=1
		For ccc.cmodel=Each cmodel
			Text 40,60+FontHeight()*(7+numbcnt+MouseZ()),ccc\loadname$
			numbcnt=numbcnt+1
		Next
		
		If control#=1 Then controls$="Keyboard"
		If control#=2 Then controls$="Mouse"
		If control#=3 Then controls$="Joy"
		If control#=4 Then controls$="UFO special JOY"
		If control#=5 Then controls$="Flight Stick"
		
		If MouseDown(1) Then
			If MouseX()>475 And MouseX()<485 And MouseY()>525 And MouseY()<535 Then
				CarToneA#=CarToneA#+10
			EndIf
			If MouseX()>495 And MouseX()<515 And MouseY()>525 And MouseY()<535 Then
				CarToneB#=CarToneB#+10
			EndIf
			If MouseX()>515 And MouseX()<525 And MouseY()>525 And MouseY()<535 Then
				CarToneC#=CarToneC#+10
			EndIf
			
			If MouseX()>475 And MouseX()<485 And MouseY()>540 And MouseY()<550 Then
				CarToneA#=CarToneA#-10
			EndIf
			If MouseX()>495 And MouseX()<515 And MouseY()>540 And MouseY()<550 Then
				CarToneB#=CarToneB#-10
			EndIf
			If MouseX()>515 And MouseX()<525 And MouseY()>540 And MouseY()<550 Then
				CarToneC#=CarToneC#-10
			EndIf	
		EndIf

			If KeyHit(5)
				chardisplay#=4
				
				CarToneA#=255
				CarToneB#=255
				CarToneC#=255
				
				ClearWorld()
				carname$="Police"
				car=LoadMesh("police.3ds")			
				ScaleMesh car,1,1,-1
				FlipMesh car
				FitMesh car,-1.5,-1,-3,3,2,6
				PositionEntity car,0,0,0
				EntityShininess car,1
				flight=False
	
				cam=CreateCamera()
				PositionEntity cam,0,0,-5								
				PointEntity cam,car
				CameraViewport cam,GraphicsWidth()/2,GraphicsHeight()/2,GraphicsWidth()/4,GraphicsHeight()/4
				CameraClsMode cam,0,1
				
			EndIf

			If KeyHit(7)
				chardisplay#=6
				
				CarToneA#=255
				CarToneB#=255
				CarToneC#=255
				
				ClearWorld()
				carname$="UFO"
				car=LoadMesh("ufo.3ds")			
				ScaleMesh car,1,1,-1
				FlipMesh car
				FitMesh car,-1.5,-1,-3,3,2,6
				PositionEntity car,0,0,0
				EntityShininess car,1
				flight=False
	
				cam=CreateCamera()
				PositionEntity cam,0,0,-5								
				PointEntity cam,car
				CameraViewport cam,GraphicsWidth()/2,GraphicsHeight()/2,GraphicsWidth()/4,GraphicsHeight()/4
				CameraClsMode cam,0,1
				
			EndIf
			If KeyHit(8) And netstatus$="Off"
				chardisplay#=7
				
				CarToneA#=255
				CarToneB#=255
				CarToneC#=255
				
		
				ClearWorld()
				carname$="Space Fighter"
				car=LoadMesh("fighter.3ds")			
				ScaleMesh car,1,1,-1
				FlipMesh car
				FitMesh car,-1.5,-1,-3,3,2,6
				PositionEntity car,0,0,0
				EntityShininess car,1
				flight=True
	
				cam=CreateCamera()
				PositionEntity cam,0,0,-5								
				PointEntity cam,car
				CameraViewport cam,GraphicsWidth()/2,GraphicsHeight()/2,GraphicsWidth()/4,GraphicsHeight()/4
				CameraClsMode cam,0,1
				
			EndIf

				If control#=4 And chardisplay#<>6 And flight=False Then
					control#=1
				EndIf
				If control#=4 And chardisplay#<>6 And flight=True Then
					control#=5
				EndIf
				If control#=5 And Flight=False Then
					control#=1
				EndIf
				If control#=6 Then
					control#=1
				EndIf

		If MouseHit(1) Then
			If chardisplay#>0 And MouseX()>40+(StringWidth("----------------------------") And MouseX()<40+(StringWidth("----------------------------  [OK]"))*FontWidth()) And MouseY()>60+FontHeight()*13 And MouseY()<60+FontHeight()*14 Then
		
	
				If chardisplay#=5 Then
					maingame(X#,YE#,PIT#,YAW#,ROLL#,HEIGH#,netstatus$,copnumber#,control#,gametype#)	
				Else
							gametype#=chardisplay#
							If netstatus$="On" Then
							maingame(0,0,0,0,0,1,"Net",0,control#,gametype#)
							Else
								
							;gamesave=WriteFile("sav\"+filename$+".sav")
							;WriteLine(gamesave,"0.0")
							;WriteLine(gamesave,"0.0")
							;WriteLine(gamesave,"3.0")
							;WriteLine(gamesave,"0.0")
							;WriteLine(gamesave,"0.0")
							;WriteLine(gamesave,"0.0")
							;WriteLine(gamesave,"70.0")
							;WriteLine(gamesave,copnumber#)
							;CloseFile(gamesave)
							maingame(0,0,0,0,0,1,netstatus$,copnumber#,control#,gametype#)
							EndIf
				EndIf
			EndIf
			numbcnt=1
			For ccc.cmodel=Each cmodel
				If MouseX()>40 And MouseX()<(StringWidth(ccc\loadname$)*FontWidth()) And MouseY()>60+FontHeight()*(7+numbcnt+MouseZ()) And MouseY()<60+FontHeight()*(8+numbcnt+MouseZ()) Then
					chardisplay#=10+numbcnt
					CarToneA#=255
					CarToneB#=255
					CarToneC#=255
					
					ClearWorld()
					carname$=ccc\loadname$
					car=LoadMesh("mods\"+ccc\loadname$+"\"+ccc\loadname$+".3ds")			
					ScaleMesh car,1,1,-1
					FlipMesh car
					FitMesh car,-1.5,-1,-3,3,2,6
					PositionEntity car,0,0,0
					EntityShininess car,1
		
					cam=CreateCamera()
					PositionEntity cam,0,0,-5								
					PointEntity cam,car
					CameraViewport cam,365,200,350,300
					CameraClsMode cam,0,1
				EndIf
				numbcnt=numbcnt+1
			Next
	
			If MouseX()>40 And MouseX()<(StringWidth("Sports Car")*FontWidth()) And MouseY()>60+FontHeight()*(0+MouseZ()) And MouseY()<60+FontHeight()*(1+MouseZ()) Then
				chardisplay#=1
				
				CarToneA#=255
				CarToneB#=255
				CarToneC#=255
				
				ClearWorld()
				carname$="Sportscar"
				car=LoadMesh("sportscar_full.3ds")			
				ScaleMesh car,1,1,-1
				FlipMesh car
				FitMesh car,-1.5,-1,-3,3,2,6
				PositionEntity car,0,0,0
				EntityShininess car,1
				flight=True
	
				cam=CreateCamera()
				PositionEntity cam,0,0,-5								
				PointEntity cam,car
				CameraViewport cam,GraphicsWidth()/2,GraphicsHeight()/2,GraphicsWidth()/4,GraphicsHeight()/4
				CameraClsMode cam,0,1
			
			EndIf
			If MouseX()>40 And MouseX()<(60+StringWidth("[Enimies: "+Str(Int(copnumber))+" ]")*FontWidth()) And MouseY()>60+FontHeight()*(6+MouseZ()) And MouseY()<60+FontHeight()*(7+MouseZ()) Then
				If netstatus$="Off" Then
					copnumber#=copnumber#+1
				EndIf
			EndIf
			If MouseX()>40 And MouseX()<(60+StringWidth("[Control: "+controls$+"]")*FontWidth()) And MouseY()>60+FontHeight()*(7+MouseZ()) And MouseY()<60+FontHeight()*(8+MouseZ()) Then
			control#=control#+1
			EndIf
			
			If MouseX()>40 And MouseX()<(StringWidth("Pickup Truck")*FontWidth()) And MouseY()>60+FontHeight()*(1+MouseZ()) And MouseY()<60+FontHeight()*(2+MouseZ()) Then
				chardisplay#=2
				
				CarToneA#=255
				CarToneB#=255
				CarToneC#=255
				
				ClearWorld()
				carname$="Pickup"
				car=LoadMesh("Pickup_full.3ds")			
				ScaleMesh car,1,1,-1
				FlipMesh car
				FitMesh car,-1.5,-1,-3,3,2,6
				PositionEntity car,0,0,0
				EntityShininess car,1
				flight=True
	
				cam=CreateCamera()
				PositionEntity cam,0,0,-5								
				PointEntity cam,car
				CameraViewport cam,GraphicsWidth()/2,GraphicsHeight()/2,GraphicsWidth()/4,GraphicsHeight()/4
				CameraClsMode cam,0,1
				
			EndIf
			If MouseX()>40 And MouseX()<(StringWidth("Convertable")*FontWidth()) And MouseY()>60+FontHeight()*(2+MouseZ()) And MouseY()<60+FontHeight()*(3+MouseZ()) Then
				
				chardisplay#=3
				
				CarToneA#=255
				CarToneB#=255
				CarToneC#=255
				
				ClearWorld()
				car=LoadMesh("convertable_full.3ds")
				carname$="Convertable"			
				ScaleMesh car,1,1,-1
				FlipMesh car
				FitMesh car,-1.5,-1,-3,3,2,6
				PositionEntity car,0,0,0
				EntityShininess car,1
				flight=0
	
				cam=CreateCamera()
				PositionEntity cam,0,0,-5								
				PointEntity cam,car
				CameraViewport cam,GraphicsWidth()/2,GraphicsHeight()/2,GraphicsWidth()/4,GraphicsHeight()/4
				CameraClsMode cam,0,1
				CameraClsColor cam,128,0,128

			EndIf
			If MouseX()>40 And MouseX()<(StringWidth("[Load "+filename$+"]")*FontWidth()) And MouseY()>60+FontHeight()*(3+MouseZ()) And MouseY()<60+FontHeight()*(4+MouseZ()) Then
				chardisplay#=5
				ClearWorld()
					If FileType("sav\"+filename$+".sav")=1 Then
							gamesave=OpenFile("sav\"+filename$+".sav")
							X#=ReadLine(gamesave)
							YE#=ReadLine(gamesave)
							gametype=ReadLine(gamesave)
							PIT#=ReadLine(gamesave)
							YAW#=ReadLine(gamesave)
							ROLL#=ReadLine(gamesave)
							HEIGH#=ReadLine(gamesave)
							copnumber#=ReadLine(gamesave)
							CloseFile(gamesave)
							ClearWorld()

				CarToneA#=255
				CarToneB#=255
				CarToneC#=255
				
				If gametype=1 Then
				car=LoadMesh("sportscar_game.3ds")
				Else If gametype=2 Then
				car=LoadMesh("Pickup_game.3ds")
				Else If gametype=3 Then
				car=LoadMesh("convertable_game.3ds")
				Else If gametype=4 Then
				car=LoadMesh("cop_game.3ds")
				Else If gametype=6 Then
				car=LoadMesh("ufo.3ds")
				Else 
					numbcnt=1
					For ccc.cmodel=Each cmodel
						If gametype=numbcnt+10 Then
							car=LoadMesh("mods\"+ccc\loadname$+"\"+ccc\loadname$+".3ds")
						EndIf
						numbcnt=numbcnt+1
					Next				
				EndIf
				ScaleMesh car,1,1,-1
				FlipMesh car
				FitMesh car,-1.5,-1,-3,3,2,6
				PositionEntity car,0,0,0
				EntityShininess car,1
	
				cam=CreateCamera()
				PositionEntity cam,0,0,-5								
				PointEntity cam,car
				CameraViewport cam,GraphicsWidth()/2,GraphicsHeight()/2,GraphicsWidth()/4,GraphicsHeight()/4
				CameraClsMode cam,0,1
				CameraClsColor cam,128,0,128
				EndIf
			EndIf
			If MouseX()>40 And MouseX()<(StringWidth("[Use Other File]")+FontWidth()) And MouseY()>60+FontHeight()*(4+MouseZ()) And MouseY()<60+FontHeight()*(5+MouseZ()) Then

					filename$=""
					While filename$=""
						
					Locate 400,50
					filename$=Input("Other Game Name: ")
					Wend
			EndIf
			If MouseX()>40 And MouseX()<(StringWidth("[Endurance Mode: "+Respawncop+"]")+FontWidth()) And MouseY()>60+FontHeight()*((-1)+MouseZ()) And MouseY()<60+FontHeight()*((0)+MouseZ()) Then
				If respawncop=True Then
					respawncop=False
				Else
					respawncop=True
				EndIf
			EndIf
		EndIf
		
		If chardisplay=1 Then
			Text (GraphicsWidth()/2),50,"Sports Car"
			Text (GraphicsWidth()/2),70,""
			Text (GraphicsWidth()/2),90,""
			Text (GraphicsWidth()/2),110,"Turn Factor: 2"
			Text (GraphicsWidth()/2),130,"Max Speed: 200 MPH"
			Text (GraphicsWidth()/2),150,"Acceleration: 0.8 MPS"
			
			TurnEntity car,0,3,0
			EntityColor car,CarToneA#,CarToneB#,CarToneC#
			UpdateWorld()
			RenderWorld()
			
		EndIf
		
		If chardisplay=2 Then 
			Text (GraphicsWidth()/2),50,"Pickup Truck"
			Text (GraphicsWidth()/2),70,""
			Text (GraphicsWidth()/2),90,""
			Text (GraphicsWidth()/2),110,"Turn Factor: 3"
			Text (GraphicsWidth()/2),130,"Max Speed: 180 MPH"
			Text (GraphicsWidth()/2),150,"Acceleration: 0.1 MPS"

			TurnEntity car,0,3,0
			EntityColor car,CarToneA#,CarToneB#,CarToneC#
			UpdateWorld()
			RenderWorld()

		EndIf
		
		
		If chardisplay=3 Then 
			;Turn#=3:Max#=0.7:accel#=0.005
			Text (GraphicsWidth()/2),50,"Convertable"
			Text (GraphicsWidth()/2),70,""
			Text (GraphicsWidth()/2),90,""
			Text (GraphicsWidth()/2),110,"Turn Factor: 3"
			Text (GraphicsWidth()/2),130,"Max Speed: 170 MPH"
			Text (GraphicsWidth()/2),150,"Acceleration: 0.05 MPS"

			TurnEntity car,0,3,0
			EntityColor car,CarToneA#,CarToneB#,CarToneC#
			UpdateWorld()
			RenderWorld()

		EndIf
		
		If chardisplay=4 Then 
			;Turn#=3:Max#=0.7:accel#=0.005
			Text (GraphicsWidth()/2),50,"Police Car"
			Text (GraphicsWidth()/2),70,"Use this police car to"
			Text (GraphicsWidth()/2),90,"track down and arrest drunk drivers."
			Text (GraphicsWidth()/2),110,"Turn Factor: 3"
			Text (GraphicsWidth()/2),130,"Max Speed: 170 MPH"
			Text (GraphicsWidth()/2),150,"Acceleration: 0.05 MPS"

			TurnEntity car,0,3,0
			EntityColor car,CarToneA#,CarToneB#,CarToneC#
			UpdateWorld()
			RenderWorld()

		EndIf
		
		If chardisplay=6 Then 
			;Turn#=3:Max#=0.7:accel#=0.005
			Text (GraphicsWidth()/2),50,"UFO"
			Text (GraphicsWidth()/2),70,"The leader mothership wants you"
			Text (GraphicsWidth()/2),90,"to hunt the human paracites."
			Text (GraphicsWidth()/2),110,"Turn Factor: 3"
			Text (GraphicsWidth()/2),130,"Max Speed: 170 MPH"
			Text (GraphicsWidth()/2),150,"Acceleration: 0.05 MPS"

			TurnEntity car,0,3,0
			EntityColor car,CarToneA#,CarToneB#,CarToneC#
			UpdateWorld()
			RenderWorld()

		EndIf
				
		If chardisplay=7 Then 
			;Turn#=3:Max#=0.7:accel#=0.005
			Text (GraphicsWidth()/2),50,"Space Fighter"
			Text (GraphicsWidth()/2),70,"You have crashed on Earth!"
			Text (GraphicsWidth()/2),90,"Flee attacking UFOs."
			Text (GraphicsWidth()/2),110,"Turn Factor: 3"
			Text (GraphicsWidth()/2),130,"Max Speed: 200 MPH"
			Text (GraphicsWidth()/2),150,"Acceleration: 0.08 MPS"

			TurnEntity car,0,3,0
			EntityColor car,CarToneA#,CarToneB#,CarToneC#
			UpdateWorld()
			RenderWorld()

		EndIf
		
			numbcnt=1
			For ccc.cmodel=Each cmodel
				If chardisplay=numbcnt+10 Then
					;Turn#=3:Max#=0.7:accel#=0.005
					Text (GraphicsWidth()/2),50,ccc\loadname$
					Text (GraphicsWidth()/2),70,""
					Text (GraphicsWidth()/2),90,""
					Text (GraphicsWidth()/2),110,"Turn Factor: 3"
					Text (GraphicsWidth()/2),130,"Max Speed: 170 MPH"
					Text (GraphicsWidth()/2),150,"Acceleration: 0.05 MPS"
		
					TurnEntity car,0,3,0
					EntityColor car,CarToneA#,CarToneB#,CarToneC#
					Flight=ccc\flightK
					UpdateWorld()
					RenderWorld()

				EndIf
				numbcnt=numbcnt+1
			Next
		
		If chardisplay=5 Then
			Text (GraphicsWidth()/2),50,"Game Type: "+gametype
			Text (GraphicsWidth()/2),70,"XLoc: "+X#
			Text (GraphicsWidth()/2),90,"YLoc: "+YE#
			Text (GraphicsWidth()/2),110,"Pitch: "+PIT#
			Text (GraphicsWidth()/2),130,"Yaw: "+YAW#
			Text (GraphicsWidth()/2),150,"Roll: "+ROLL#
		

			TurnEntity car,0,3,0
			EntityColor car,CarToneA#,CarToneB#,CarToneC#
			UpdateWorld()
			RenderWorld()

		EndIf
		
			If MouseHit(2) Then
				If MouseX()>40 And MouseX()<(60+StringWidth("[Enimies: "+Str(Int(copnumber))+" ]")*FontWidth()) And MouseY()>60+FontHeight()*(6+MouseZ()) And MouseY()<60+FontHeight()*(7+MouseZ()) Then
					copnumber#=copnumber#-1
				EndIf
			EndIf

		DrawImage gfxpoint,MouseX(),MouseY(),pointrot#
		pointrot#=pointrot#+1
		If pointrot#=4 Then pointrot#=0
		Flip
		
		If KeyHit(1) Then mainmenu()
		
	Forever
	
End Function


;-------------------------- OPTIONS MENU FUNCTION IS HERE --------------------------

Function options()
Font=LoadFont("Microsoft Sans Serif",20,1,0,0)
SetFont Font
Color 255,0,0

FlushMouse()
FlushKeys()

StopChannel(bgm)

ResizeImage gfxbackground,GraphicsWidth(),GraphicsHeight()

Repeat
Cls
Color 255,0,0

If KeyHit(65) Then
	scrshot()
EndIf


	If Not ChannelPlaying(bgm) Then
		bgm=PlayMusic("options.wma")
	EndIf
	
DrawImage gfxbackground,0,0
DrawImage gfxoptionsback,0,0

;Text 0,0,"X: "+MouseX()
;Text 0,10,"Y: "+MouseY()

Text 67,82,"Your Name: "+name$
Text 67,112,"Screen Mode: "+GraphicsWidth()+"x"+GraphicsHeight()
Text 67,142,"Your gamename: "+filename$
Text 67,172,"Credits"

If MouseHit(1) Then

If MouseX()>67 And MouseX()<305 And MouseY()>172 And MouseY()<192 Then
credits()
EndIf

If MouseX()>67 And MouseX()<305 And MouseY()>82 And MouseY()<92 Then
name$=""

Cls
DrawImage gfxbackground,0,0
DrawImage gfxoptionsback,0,0
If KeyHit(65) Then
	scrshot()
EndIf
Flip

While name$=""

Locate 67,82
FlushKeys()
FlushMouse()
name$=Input("Your Name: ")
Wend

EndIf

If MouseX()>67 And MouseX()<305 And MouseY()>142 And MouseY()<152 Then
filename$=""

Cls
DrawImage gfxbackground,0,0
DrawImage gfxoptionsback,0,0
Flip

While filename$=""

Locate 67,142
FlushKeys()
FlushMouse()
filename$=Input("Your gamename: ")
Wend

EndIf
EndIf

If KeyHit(1) Then
mainmenu()
EndIf

DrawImage gfxpoint,MouseX(),MouseY(),pointrot#
pointrot#=pointrot#+1
If pointrot#=4 Then pointrot#=0

If KeyHit(1) Then mainmenu()
Flip
Forever

End Function

Function credits()
FlushKeys()
FlushMouse()

StopChannel(bgm)

Repeat

Cls


	If Not ChannelPlaying(bgm) Then
		bgm=PlayMusic("credits.wma")
	EndIf
	

DrawImage gfxbackground,0,0

Text GraphicsWidth()/2,A,"--Renegade--",1
Text GraphicsWidth()/2,B,"Created by the Nate Works Gamming Group",1
Text GraphicsWidth()/2,BB,"www.freewebs.com/nateworks",1
Text GraphicsWidth()/2,C,"Programming by Sonic~Waves[TM]",1
Text GraphicsWidth()/2,D,"Car meshes by Tim Rich & Sonic~Waves[TM].",1
Text GraphicsWidth()/2,E,"City Mesh By Sonic~Waves[TM]",1
Text GraphicsWidth()/2,F,"Concept by Sonic~Waves[TM] and Tim",1
Text GraphicsWidth()/2,G,"Game soundfx from random sources",1
Text GraphicsWidth()/2,H,"In game music by Sonic~Waves[TM] and affiliations",1
Text GraphicsWidth()/2,I,"Special thanks to all who contributed!",1
Text GraphicsWidth()/2,J,"Credits Reward: press 4 or 7 key on the keyboard on the Select Car Screen!",1

A=A-1
B=B-1
BB=BB-1
C=C-1
D=D-1
E=E-1
F=F-1
G=G-1
H=H-1
I=I-1
J=J-1

If A=<-10 Then A=610
If B=<-10 Then B=610
If BB=<-10 Then BB=610
If C=<-10 Then C=610
If D=<-10 Then D=610
If E=<-10 Then E=610
If F=<-10 Then F=610
If G=<-10 Then G=610
If H=<-10 Then H=610
If I=<-10 Then I=610
If J=<-10 Then J=610

If KeyHit(1) Then
mainmenu()
EndIf
Flip

Forever

End Function

Function maingame(X#,y#,PIT#,YAW#,ROLL#,HEIGH#,netstatus$,copnumber#,control#,gametype#)

If netstatus$="On" Or netstatus$="Net" Then
Cls
Text 0,0,"If you don't see the create new game window, use Alt + Tab until you do."
Flip
EndIf

If control#=4 Then
	falldam=0
Else
	falldam=1
EndIf

hits#=0
track=0

info("Welcome To Renegade")

StopChannel(bgm)

If netstatus$="On" Then
	gamehosted=HostNetGame(filename$)
	If gamehosted<2 Then mainmenu()
	playerID=CreateNetPlayer(carname$+"="+name$)
	If playerID=0 Then
	info("Error - no player created")
	savelog()
	RuntimeError("Error - no player created")
	EndIf

Else If netstatus$="Net" Then
	gamehosted=StartNetGame()
	If gamehosted=0 Then mainmenu()
	
	playerID=CreateNetPlayer(carname$+"="+name$)
	If playerID=0 Then
	info("Error - no player created")
	savelog()
	RuntimeError("Error - no player created")
	EndIf

EndIf

CHNSFX=PlaySound(SFXStart)

FlushMouse()
FlushKeys()
ClearWorld()

Local chat$

Collisions BODY,NET,2,1
Collisions BODY,POLICE,2,3
Collisions BODY,SCENE,2,2

Collisions WHEEL,SCENE,2,2

Collisions TYPE_BULLET,POLICE,2,3
Collisions TYPE_BULLET,SCENE,2,3
Collisions TYPE_BULLET,NET,2,3
Collisions TYPE_BULLET,TYPE_BOT,2,3

Collisions NET_BULLET,BODY,2,3
Collisions NET_BULLET,POLICE,2,3
Collisions NET_BULLET,SCENE,2,3

Collisions CAMBOX,TYPE_GRASS,2,2

Collisions NET,BODY,2,1

Collisions POLICE,SCENE,2,2
Collisions POLICE,BODY,2,1
Collisions POLICE,NET,2,2
Collisions POLICE,POLICE,2,2
Collisions POLICE,TYPE_BOT,2,2

Collisions TYPE_BOT,SCENE,2,2
Collisions TYPE_BOT,BODY,2,1
Collisions TYPE_BOT,NET,2,2
Collisions TYPE_BOT,POLICE,2,2
Collisions TYPE_BOT,TYPE_BOT,2,2

Collisions TYPE_ENEMYBULL,BODY,2,3
Collisions TYPE_BULLET,SCENE,2,3
Collisions TYPE_BULLET,NET,2,3

Collisions GHOST,TYPE_GRASS,2,2

city=LoadMesh("city.3ds")
EntityType city,SCENE

For count=-6 To 6
copy[count+6]=CopyEntity(city)
EntityType copy[count+6],SCENE
PositionEntity copy[count+6],0,0,count*250
copy[count+94]=CopyEntity(city)
EntityType copy[count+94],SCENE
PositionEntity copy[count+94],-250,0,count*250
RotateEntity copy[count+94],0,180,0
Next

HideEntity city

wall1=CreateCube()
PositionEntity wall1,-125,.5,1625
ScaleEntity wall1,200,1,1
tex=LoadTexture("Rock02.jpg")
EntityTexture wall1,tex
EntityType wall1,SCENE

wall2=CreateCube()
PositionEntity wall2,-125,1,-1625
ScaleEntity wall2,200,2,1
tex=LoadTexture("Rock02.jpg")
EntityTexture wall2,tex
EntityType wall2,SCENE


showwheel=True

laser=True
flight=0

If gametype=1 Then
car=LoadMesh( "sportscar_game.3ds" )
Maxspeed#=2.0
Accel#=0.008
TurnFactor#=2
EndIf

If gametype=2 Then
car=LoadMesh( "Pickup_game.3ds" )
Maxspeed#=1.8
Accel#=0.006
TurnFactor#=3	
EndIf

;Turn#=3:Max#=0.7:accel#=0.005
If gametype=3 Then
car=LoadMesh( "convertable_game.3ds" )
Maxspeed#=1.7
Accel#=0.005
TurnFactor#=3
EndIf

If gametype=4 Then
car=LoadMesh( "cop_game.3ds" )
Maxspeed#=1.7
Accel#=0.005
TurnFactor#=3
EndIf

If gametype=6 Then
car=LoadMesh( "UFO.3ds" )
Maxspeed#=1.7
Accel#=0.005
TurnFactor#=3
info("You have landed!")
showwheel=False
laser=True
EndIf

If gametype=7 Then
car=LoadMesh( "fighter.3ds")
Maxspeed#=2
Accel#=0.008
TurnFactor#=3
showwheel=False
laser=True
flight=1
EndIf

numbcnt=1
For ccc.cmodel=Each cmodel
	If gametype=numbcnt+10 Then
		carload$="mods\"+ccc\loadname$+"\"+ccc\loadname$+".3ds"
		showwheel=ccc\wheel
		laser=ccc\whoop
	EndIf
	numbcnt=numbcnt+1
Next

If gametype>9 Then
car=LoadMesh(carload$)
Maxspeed#=1.7
Accel#=0.005
TurnFactor#=3
EndIf

EntityColor car,CarToneA#,CarToneB#,CarToneC#
ScaleMesh car,1,1,-1
FlipMesh car
FitMesh car,-1.5,-1,-3,3,2,6,True
PositionEntity car,X#,HEIGH#,Y#
RotateEntity car,PIT#,YAW#,ROLL#
EntityShininess car,1
EntityType car,BODY

grass=CreatePlane()
EntityType grass,SCENE
EntityOrder grass,1
tex=LoadTexture("grass.jpg")
ScaleTexture tex,50,50
EntityTexture grass,tex
FreeTexture tex

grassb=CreatePlane()
EntityType grassb,Type_GRASS
EntityAlpha grassb,0

camera=CreateCamera()
EntityType camera,CAMBOX

ear=CreateListener(Camera,.01,1,.1)

mapcam=CreateCamera()
PositionEntity mapcam,0,100,0
RotateEntity mapcam,90,0,0
CameraViewport mapcam,(GraphicsWidth()-(GraphicsWidth()/4)),(GraphicsHeight()-(GraphicsHeight()/4)),GraphicsWidth()/4,GraphicsHeight()/4

;make it bright (add light)
light=CreateLight()
TurnEntity light,45,45,0

worldtime#=Left$(CurrentTime$(),2)
If worldtime#>12 Then worldtime#=worldtime#-24

If worldtime#>9 Or worldtime#<-9 Then
ShowEntity light
CameraClsColor camera,0,128,255
Else
HideEntity light
CameraClsColor camera,0,0,0
EndIf

If gametype=4 Or gametype=6
	If copnumber#>0 Then
		For bbb=1 To copnumber#
			createbot()
		Next
	EndIf
Else 
	If copnumber#>0 Then
		For bbb=1 To copnumber#
		If flight=1 Then
			createufo()
		Else
			createcop()
		EndIf
		Next
	EndIf
EndIf

target=CreatePivot( car )
PositionEntity target,0,5,-12

speed#=0
x_vel#=0:prev_x#=EntityX( car )
y_vel#=0:prev_y#=EntityY( car )
z_vel#=0:prev_z#=EntityZ( car )


;Add NPCs

createstealth()

If gamehosted=1 Then
	createsaucer()
Else
If Rand(1,100)=50 Then
	createsaucer()
EndIf
EndIf

cnt=1
For z#=2 To -2 Step -4
For x#=-1.5 To 1.5 Step 3
	whtext=LoadTexture("wheeltex.bmp")
	wheels[cnt]=LoadMesh("wheel.3ds",car)
	EntityTexture wheels[cnt],whtext
		If showwheel=False Then
			EntityAlpha wheels[cnt],0
		Else
			EntityAlpha wheels[cnt],1
		EndIf
	EntityColor wheels[cnt],128,128,128
	ScaleEntity wheels[cnt],.5,.5,.5
	EntityRadius wheels[cnt],.5
	PositionEntity wheels[cnt],x,0,z
	EntityType wheels[cnt],WHEEL
	cnt=cnt+1
Next
Next

While ChannelPlaying(CHNSFX)
Cls
	;resposition wheels
	cnt=1
	For z=1.7 To -1.7 Step -3.4
	For x=-1.1 To 1.1 Step 2.2
;		PositionEntity wheels[cnt],0,0,0
;		ResetEntity wheels[cnt]
		PositionEntity wheels[cnt],x,-1,z
		cnt=cnt+1
	Next
	Next
	;update camera
	If speed>=0	
		dx#=EntityX( target,True )-EntityX( camera )
		dy#=EntityY( target,True )-EntityY( camera )
		dz#=EntityZ( target,True )-EntityZ( camera )
		TranslateEntity camera,dx*.1,dy*.1,dz*.1
	EndIf
	PointEntity camera,car

RenderWorld()
Flip
Wend

;start main loop -------------------------------------------

While Not KeyHit(1)

If EntityCollided (Car,POLICE) Then
If god=0 Then
	hits=hits+1
	info("Collision damage: "+Int(hits*10)+"%")
EndIf
EndIf

If KeyHit(65) Then
	scrshot()
EndIf

	If Not ChannelPlaying(bgm) Then
	track=track+1
		For tx.track=Each track
			If tx\number=track Then
				bgm=PlayMusic(tx\loadname$)
				info("Playing track "+Int(track)+" of "+Int(Str$(maxtrax#)))
			EndIf
		Next
	EndIf

If track>maxtrax# Then
	track=0
EndIf


If hits#=>10 Then
	CHNSFX=PlaySound(TDSFXAHHHH)
	info("Your car was destroyed!")
	If netstatus$="On" Or netstatus$="Net" Then
		SendNetMsg 5, "I was destroyed!",PlayerID,0,0
		DeleteNetPlayer(PlayerID)
		StopNetGame()
	EndIf
For cc.cop=Each cop
	Delete cc
Next
For np.NPC=Each NPC
	Delete np
Next
For u.UFO=Each UFO
	Delete u
Next
For bu.Bullet=Each Bullet
	Delete bu
Next
For sp.Spark=Each Spark
	Delete sp
Next
For plr.player=Each player
	Delete plr
Next

	FreeEntity car
	FreeEntity camera
	If lightex=1 Then
		FreeEntity light
	EndIf
	FreeEntity grass
	FreeEntity city
	ClearWorld()
		;file=WriteFile("log.txt")
		;WriteLine(file,"Name="+name$)
		;WriteLine(file,"gamesave="+filename$)
		;WriteLine(file,"mode#="+mode#)
		;WriteLine(file,"control#="+control#)
		;WriteLine(file,"Renegade log file")
		;WriteLine(file,"Log created at "+CurrentTime()+" on "+CurrentDate())
		;WriteLine(file,"--------------End of log--------------")
		;For inf.Info=Each Info
		;WriteLine(file,inf\txt$)
		;Next
		;WriteLine(file,"-------------Start of log-----------------")
		;CloseFile file
	mainmenu()
EndIf

	;align car to wheels
	zx#=(EntityX( wheels[2],True )+EntityX( wheels[4],True ))/2
	zx=zx-(EntityX( wheels[1],True )+EntityX( wheels[3],True ))/2
	zy#=(EntityY( wheels[2],True )+EntityY( wheels[4],True ))/2
	zy=zy-(EntityY( wheels[1],True )+EntityY( wheels[3],True ))/2
	zz#=(EntityZ( wheels[2],True )+EntityZ( wheels[4],True ))/2
	zz=zz-(EntityZ( wheels[1],True )+EntityZ( wheels[3],True ))/2
	AlignToVector car,zx,zy,zz,1
	
	zx#=(EntityX( wheels[1],True )+EntityX( wheels[2],True ))/2
	zx=zx-(EntityX( wheels[3],True )+EntityX( wheels[4],True ))/2
	zy#=(EntityY( wheels[1],True )+EntityY( wheels[2],True ))/2
	zy=zy-(EntityY( wheels[3],True )+EntityY( wheels[4],True ))/2
	zz#=(EntityZ( wheels[1],True )+EntityZ( wheels[2],True ))/2
	zz=zz-(EntityZ( wheels[3],True )+EntityZ( wheels[4],True ))/2
	AlignToVector car,zx,zy,zz,3
	
	;calculate car velocities	
	cx#=EntityX( car ):x_vel=cx-prev_x:prev_x=cx
	cy#=EntityY( car ):y_vel=cy-prev_y:prev_y=cy
	cz#=EntityZ( car ):z_vel=cz-prev_z:prev_z=cz
	
	If control#=1 Then keyboardcontrol(car,netstatus$,PlayerID)
	If control#=2 Then mousecontrol(car,netstatus$,PlayerID)
	If control#=3 Then joycontrol(car,netstatus$,PlayerID)
	If control#=4 Then UFOcontrol(car,netstatus$,PlayerID)
	If control#=5 Then FlightStick(car,netstatus$,PlayerID)

	
	For bul.Bullet=Each Bullet
		UpdateBullet( bul )
	Next
	
	For s.Spark=Each Spark
		UpdateSpark( s )
	Next

	For co.Cop=Each Cop
		UpdateCop( co )
	Next
	
	For cot.Bot=Each Bot
		UpdateRBot( cot )
	Next

	For np.Npc=Each Npc
		UpdateNpc( np )
	Next
	
	For uf.UFO=Each UFO
		UpdateUFO( uf )
	Next


	;-------------- hangtime and sound stuff --------------
	LastTouch=Touch
	If EntityCollided(car,TYPE_CITY) Or EntityCollided(car,Scene) Then
	If HangTime>100 And falldam=1 And god=0 Then
	EmitSound(sfxCrash,Car)
	hits=hits+1
	info("Falling damage: "+Int(hits*10)+"%")
	EndIf
	
	If LastHang<HangTime Then LastHang=HangTime
	HangTime=0
	Touch=1
	EndIf

	If Not EntityCollided(car,TYPE_CITY) Or EntityCollided(car,Scene) Then
	Touch=2
	HangTime=HangTime+1
	EndIf
	
	;control stuff-------------------------------
	
	;resposition wheels
	cnt=1
	For z=1.7 To -1.7 Step -3.4
	For x=-1.1 To 1.1 Step 2.2
;		PositionEntity wheels[cnt],0,0,0
;		ResetEntity wheels[cnt]
		PositionEntity wheels[cnt],x,-1,z
		cnt=cnt+1
	Next
	Next

		If KeyHit(68) Then
		Cls
		info("Game was paused.")
			If netstatus$="On" Or netstatus$="Net" Then
				SendNetMsg 2,"I Paused!",PlayerID,0
			EndIf
		RenderWorld()
		Text GraphicsWidth()/2,GraphicsHeight()/2,"Game Paused",1,1
		Text GraphicsWidth()/2,(GraphicsHeight()/2)+FontHeight(),"Press any key to continue!",1,1
		Flip
		WaitKey()
		EndIf
	
	If netstatus$="On" Or netstatus$="Net" Then
		UpdateNetwork()
		SendNetMsg 1,PackPlayerMsg$(),PlayerID,0
	EndIf
	

	worldtime#=Left$(CurrentTime$(),2)
	If worldtime#>12 Then worldtime#=worldtime#-24
	
	If worldtime#>6 Or worldtime#<-6 Then
	If lightex=0 Then
	lightex=1
	light=CreateLight()
	TurnEntity light,45,45,0
	EndIf
	CameraClsColor camera,0,128,255
	Else
	If lightex=1 Then
	lightex=0
	HideEntity light
	FreeEntity light
	EndIf
	CameraClsColor camera,0,0,0
	EndIf
	
	;kaboom.wma
	
	PositionEntity mapcam,EntityX(car),EntityY(car)+50,EntityZ(car)
	RotateEntity mapcam,90,EntityYaw(car),0
	
	;update camera
	If speed>=0	
		dx#=EntityX( target,True )-EntityX( camera )
		dy#=EntityY( target,True )-EntityY( camera )
		dz#=EntityZ( target,True )-EntityZ( camera )
		TranslateEntity camera,dx*.1,dy*.1,dz*.1
	EndIf
	PointEntity camera,car
	
	UpdateWorld
	RenderWorld

	For p.player=Each player
		If EntityInView(p\CarO,Camera) Then
			CameraProject(camera,EntityX(p\carO),EntityY(p\carO),EntityZ(p\carO))
			Text ProjectedX#(),ProjectedY#()-FontHeight(),p\name$,1,1
			If p\msg$<>"" Then
				Text ProjectedX#(),ProjectedY#(),"said: "+p\msg$,1,1
			EndIf
			p\msgdelay#=p\msgdelay#+1
			If p\msgdelay#=200 Then p\msg$=""
		EndIf
	Next

If consoleout=False Then
	Color 0,0,0 ;text shadow
	Text 2,FontHeight()*0,"Name: "+name$
	Text 2,FontHeight()*1,"Speed: "+speed*100
	Text 2,FontHeight()*2,"Time: "+CurrentTime$()
	y=FontHeight()*3
	r=255
	inf.Info=First info
		If r>0
			Text 2,y,"Info: "+inf\txt$
			y=y-FontHeight()
			r=r-12
		EndIf

	Color 255,255,255
	Text 0,FontHeight()*0,"Name: "+name$
	Text 0,FontHeight()*1,"Speed: "+speed*100
	Text 0,FontHeight()*2,"Time: "+CurrentTime$()
	y=FontHeight()*3
	r=255
	inf.Info=First info
		If r>0
			Text 0,y,"Info: "+inf\txt$
			y=y-FontHeight()
			r=r-12
		EndIf
	Text 0,FontHeight()*4,">"+chat$
	
Else
	Color 0,0,0 ;console shadow
	Text 2,FontHeight()*8,">"+chat$
	y=FontHeight()*7
	r=255
	For inf.Info=Each Info
		If r>0
			Text 2,y,inf\txt$
			y=y-FontHeight()
			r=r-12
		EndIf
	Next
	
	Color 255,255,255
	Text 0,FontHeight()*8,">"+chat$
	y=FontHeight()*7
	r=255
	For inf.Info=Each Info
		If r>0
			Text 0,y,inf\txt$
			y=y-FontHeight()
			r=r-12
		EndIf
	Next
EndIf

If KeyHit(15) Then ;key86?
If consoleout=True Then
consoleout=False
Else
consoleout=True
EndIf
EndIf

			;Chat stuff
			key=GetKey()
			If key
				If key=13
					If chat$<>"" Then
					If Instr(chat$,"=")>0 Then
						command(Left(chat$,Instr(chat$,"=")-1),Mid(chat$,Instr(chat$,"=")+1))
						chat$=""
					Else	
						If netstatus$="On" Or netstatus$="Net" Then
						SendNetMsg 2,chat$,PlayerID,0,0
						EndIf
						info("You Said: "+Chat$)
						chat$=""
						EndIf
					EndIf
				Else If key=8
					If Len(chat$)>0 Then chat$=Left$(chat$,Len(chat$)-1)
				Else If key>=32 And key<127
					chat$=chat$+Chr$(key)
				EndIf
				
			EndIf

If hits#>9 Then
showhits#=10
Else
showhits#=hits#
EndIf

DrawImage Damage,0,GraphicsHeight()-ImageHeight(Damage),Int(showhits#)

WaitTimer(timer)
Flip
Wend

If netstatus$="On" Or netstatus$="Net" Then
	SendNetMsg 5, "I quit!",PlayerID,0,0
	DeleteNetPlayer(PlayerID)
	StopNetGame()
EndIf

	;gamesave=WriteFile("sav\"+filename$+".sav")
	;WriteLine(gamesave,EntityX#(car))
	;WriteLine(gamesave,EntityZ#(car))
	;WriteLine(gamesave,gametype#)
	;WriteLine(gamesave,EntityPitch#(car))
	;WriteLine(gamesave,EntityYaw#(car))
	;WriteLine(gamesave,EntityRoll#(car))
	;WriteLine(gamesave,EntityY#(car))
	;WriteLine(gamesave,EntityY#(car))
	;WriteLine(gamesave,EntityY#(car))
	;CloseFile(gamesave)

If netstatus$="On" Or netstatus$="Net" Then
	StopNetGame()
EndIf

FreeEntity car
FreeEntity camera

If lightex=1 Then
FreeEntity light
EndIf

For cc.cop=Each cop
	Delete cc
Next
For np.NPC=Each NPC
	Delete np
Next
For u.UFO=Each UFO
	Delete u
Next
For bu.Bullet=Each Bullet
	Delete bu
Next
For sp.Spark=Each Spark
	Delete sp
Next
For plr.player=Each player
	Delete plr
Next

FreeEntity grass
FreeEntity city
ClearWorld()
mainmenu()
End Function

;end of main game function

Function PackPlayerMsg$()
	Return Left(LSet$(Str$(EntityX(car)),8),8) + Left(LSet$(Str$(EntityZ(car)),8),8) + Left(LSet$(Str$(EntityYaw(car)),8),8) + Left(LSet$(Str$(EntityY(car)),8),8) + Left(LSet$(Str$(Int(EntityPitch(car))),8),8) + Left(LSet$(Str$(EntityRoll(car)),8),8) + LSet$(gametype#,8) + LSet$( Int(cartoneA#),8) + LSet$( Int(cartoneB#),8) + LSet$( Int(cartoneC#),8)
End Function

Function UpdateNetwork.player()
	While RecvNetMsg()
		Select NetMsgType()
		Case 1:
			getname$=Mid(NetPlayerName$(NetMsgFrom()),Instr(NetPlayerName$(NetMsgFrom()),"=")+1)
			cartype$=Left(NetPlayerName$(NetMsgFrom()),Instr(NetPlayerName$(NetMsgFrom()),"=")-1)
			p.player=FindPlayer( NetMsgFrom() )
			If p<>Null Then UnpackPlayerMsg( NetMsgData$(),p,getname$)
		Case 2:
			getname$=Mid(NetPlayerName$(NetMsgFrom()),Instr(NetPlayerName$(NetMsgFrom()),"=")+1)
			cartype$=Left(NetPlayerName$(NetMsgFrom()),Instr(NetPlayerName$(NetMsgFrom()),"=")-1)
			p.player=FindPlayer( NetMsgFrom() )
			EmitSound(sfxbeep,p\CarO)
			p\msg$=NetMsgData$()
			p\msgdelay#=0
			Info(getname$+" Said:"+NetMsgData$())
		Case 3:
			getname$=Mid(NetPlayerName$(NetMsgFrom()),Instr(NetPlayerName$(NetMsgFrom()),"=")+1)
			cartype$=Left(NetPlayerName$(NetMsgFrom()),Instr(NetPlayerName$(NetMsgFrom()),"=")-1)
			p.player=FindPlayer( NetMsgFrom() )
		Case 4:
			p.player=FindPlayer( NetMsgFrom() )
			createbullet(p\carO,NET_BULLET,0)

		Case 5:
			p.player=FindPlayer( NetMsgFrom() )
			EmitSound(SFXAHHHH,p\carO)
			createspark(p\carO)
							
		Case 8:
			getname$=Mid(NetPlayerName$(NetMsgFrom()),Instr(NetPlayerName$(NetMsgFrom()),"=")+1)
			cartype$=Left(NetPlayerName$(NetMsgFrom()),Instr(NetPlayerName$(NetMsgFrom()),"=")-1)
			info(NetMsgData$())
		
		Case 9:
			getname$=Mid(NetPlayerName$(NetMsgFrom()),Instr(NetPlayerName$(NetMsgFrom()),"=")+1)
			cartype$=Left(NetPlayerName$(NetMsgFrom()),Instr(NetPlayerName$(NetMsgFrom()),"=")-1)
			Cheat$=NetMsgData$()

			info(getname$+NetMsgFrom()+" Used The Cheat Code: "+cheat$)
			
		Case 100:
			getname$=Mid(NetPlayerName$(NetMsgFrom()),Instr(NetPlayerName$(NetMsgFrom()),"=")+1)
			cartype$=Left(NetPlayerName$(NetMsgFrom()),Instr(NetPlayerName$(NetMsgFrom()),"=")-1)

			p.Player=New player
			p\net_id=NetMsgFrom()
			p\car$=cartype$
			p\name$=getname$
			info(getname$+" has joined the game. ")
			

				If p\car$="Sportscar"	Then
					p\carO=LoadMesh("Sportscar_full.3ds")			
					ScaleMesh p\carO,1,1,-1
					FlipMesh p\carO
					FitMesh p\carO,-1.5,-1,-3,3,2,6
					EntityShininess p\carO,1
					PositionEntity p\carO,0,100,0
					EntityType p\carO,NET
				Else If p\car$="Convertable" Then
					p\carO=LoadMesh("convertable_full.3ds")			
					ScaleMesh p\carO,1,1,-1
					FlipMesh p\carO
					FitMesh p\carO,-1.5,-1,-3,3,2,6
					EntityShininess p\carO,1
					PositionEntity p\carO,0,100,0
					EntityType p\carO,NET
				Else If p\car$="Pickup" Then
					p\carO=LoadMesh("Pickup_full.3ds")			
					ScaleMesh p\carO,1,1,-1
					FlipMesh p\carO
					FitMesh p\carO,-1.5,-1,-3,3,2,6
					EntityShininess p\carO,1
					PositionEntity p\carO,0,100,0
					EntityType p\carO,NET
				Else If p\car$="Police" Then
					p\carO=LoadMesh("Police.3ds")			
					ScaleMesh p\carO,1,1,-1
					FlipMesh p\carO
					FitMesh p\carO,-1.5,-1,-3,3,2,6
					EntityShininess p\carO,1
					PositionEntity p\carO,0,100,0
					EntityType p\carO,POLICE
				Else If p\car$="UFO" Then
					p\carO=LoadMesh("UFO.3ds")			
					ScaleMesh p\carO,1,1,-1
					FlipMesh p\carO
					FitMesh p\carO,-1.5,-1,-3,3,2,6
					EntityShininess p\carO,1
					PositionEntity p\carO,0,100,0
					EntityType p\carO,POLICE
				Else
					looo$=""
					numbcnt=1
					For ccc.cmodel=Each cmodel
						If Lower(cartype$)=Lower(ccc\loadname$) Then
							looo$="mods\"+ccc\loadname$+"\"+ccc\loadname$+".3ds"
						EndIf
						numbcnt=numbcnt+1
					Next
					
					If looo$<>"" Then
						p\carO=LoadMesh(looo$)
						ScaleMesh p\carO,1,1,-1
						FlipMesh p\carO
						FitMesh p\carO,-1.5,-1,-3,3,2,6
						EntityShininess p\carO,1
						PositionEntity p\carO,0,100,0
						EntityType p\carO,NET
					Else
						p\carO=LoadMesh("m_car01.3DS")			
						ScaleMesh p\carO,1,1,-1
						FlipMesh p\carO
						FitMesh p\carO,-1.5,-1,-3,3,2,6
						EntityShininess p\carO,1
						PositionEntity p\carO,0,100,0
						EntityType p\carO,NET
					EndIf
				EndIf
				EmitSound(sfxSpawn,p\carO)
			
		Case 101:
			p.Player=FindPlayer( NetMsgFrom() )
			HideEntity p\CarO
			If p<>Null
				info("A player has left the game. ")
				Delete p
			EndIf
			
			
		Case 102:
		
		
			Text 1,50, "I'm the New host! "
			gamehosted=2
		
		Case 200:
		
		
			EndGraphics
			Print "The session has been lost!"
			WaitKey
			End
		
		
		End Select
	Wend
End Function

;find player with player id
Function FindPlayer.Player( id )
	For p.Player=Each Player
		If p\net_id=id Then Return p
	Next
End Function

;unpack player details from a string
Function UnpackPlayerMsg.player(msg$,p.player,who$)
	X#=Mid$( msg$,1,8 )
	Z#=Mid$( msg$,9,8 )
	Yaw#=Mid$( msg$,17,6 )
	Y#=Mid$( msg$,25,6 )
	Pitch#=Mid$( msg$,33,6 )
	Roll#=Mid$( msg$,41,6 )

	CarTp#=Mid$( msg$,49,2 )
	CartToneA#=Mid$( msg$,57,6 )
	CartToneB#=Mid$( msg$,65,6 )
	CartToneC#=Mid$( msg$,73,6 ) 

	EntityColor p\CarO,CartToneA#,CartToneB#,CartToneC#
	PositionEntity p\CarO,X,Y,Z
	RotateEntity p\CarO,Pitch,Yaw,Roll
End Function

Function CreateCop.Cop()
	co.Cop=New Cop
				co\CopBOdy=LoadMesh("police.3ds")			
				ScaleMesh co\CopBOdy,1,1,-1
				FlipMesh co\CopBOdy
				FitMesh co\CopBOdy,-1.5,-1,-3,3,2,6
				EntityShininess co\CopBOdy,1
				PositionEntity co\Copbody,Rand(-100,100),50,Rand(-1000,1000)
				EntityType co\copbody,POLICE
	Return co
End Function

Function CreateUFO.UFO()
	co.UFO=New UFO
				co\CopBOdy=LoadMesh("UFO.3ds")			
				ScaleMesh co\CopBOdy,1,1,-1
				FlipMesh co\CopBOdy
				FitMesh co\CopBOdy,-1.5,-1,-3,3,2,6
				EntityShininess co\CopBOdy,1
				PositionEntity co\Copbody,Rand(-100,100),200,Rand(-1000,1000)
				EntityColor co\copbody,Rand(0,255),Rand(0,255),Rand(0,255)
				EntityType co\copbody,POLICE
	Return co
End Function

Function CreateBot.Bot()
	co.Bot=New Bot
				co\bBOdy=LoadMesh("convertable_full.3ds")			
				ScaleMesh co\bBOdy,1,1,-1
				FlipMesh co\bBOdy
				FitMesh co\bBOdy,-1.5,-1,-3,3,2,6
				EntityShininess co\bBOdy,1
				PositionEntity co\bbody,Rand(-100,100),50,Rand(-1000,1000)
				EntityType co\bbody,TYPE_BOT
				EntityColor co\bbody,Rand(0,255),Rand(0,255),Rand(0,255)
	Return co
End Function

;update cop stuff
Function UpdateCop.cop( co.Cop )
	If EntityCollided(co\copbody,BODY) Then
	MoveEntity co\copbody,0,0,-8
	EndIf

	If EntityCollided(co\copbody,SCENE) Then
	MoveEntity co\copbody,0,0,1.6
	TurnEntity co\copbody,0,DeltaYaw#(co\copbody,car),0
	Else
	TranslateEntity co\copbody,0,gravity*10,0
	EndIf

If Not ChannelPlaying(co\channel)
	co\channel=EmitSound(SFXSIREN,co\COPBODY)
EndIf

If EntityCollided(co\copbody,type_bullet) Then
HideCop(co)
info("Cop destroyed!")
EndIf
End Function

;update cop stuff
Function UpdateUFO.UFO( co.UFO )

	If EntityCollided(co\copbody,BODY) Then
		MoveEntity co\copbody,0,0,-8
	EndIf
	
If Not ChannelPlaying(co\channel)
	co\channel=EmitSound(sfxMODULAT,co\COPBODY)
EndIf

co\reload=co\reload+1

If EntityDistance(co\copbody,car)>50 And co\reload>10 Then
	co\reload=0
	createbullet(co\copbody,TYPE_ENEMYBULL,0)
EndIf
	
MoveEntity co\copbody,0,0,1.6
PointEntity co\copbody,car
If EntityCollided(co\copbody,type_bullet) Then
HideUFO(co)
info("UFO destroyed!")
EndIf
End Function

;update cop stuff
Function UpdateRBot.bot( co.Bot )

If EntityDistance(co\bbody,car)<80 Then
	MoveEntity co\bbody,0,0,1.2
	TurnEntity co\bbody,0,180+DeltaYaw#(co\bbody,car),0
Else
	If EntityDistance(co\bbody,car)>900 Then
		PointEntity co\bbody,car
	Else
		TurnEntity co\bbody,0,Rand(-5,5),0
		MoveEntity co\bbody,0,0,.8
	EndIf
EndIf

If Not ChannelPlaying(co\channel)
	co\channel=EmitSound(Engine3D,co\bBODY)
EndIf

TranslateEntity co\bbody,0,gravity*10,0
If EntityCollided(co\bbody,type_bullet) Or EntityCollided(co\bbody,BODY) Then
	hideRBot(co)
	info("A renegade was put in his place!")
EndIf
End Function

Function HideRBot.Bot(co.bot)
		If respawncop=True Then
			createBot()
		EndIf
		HideEntity co\bbody
		Delete co
End Function

Function HideCop.cop(co.cop)
		If respawncop=True Then
			createcop()
		EndIf
		HideEntity co\copbody
		Delete co
End Function

Function HideUFO.cop(co.UFO)
		If respawncop=True Then
			createUFO()
		EndIf
		HideEntity co\copbody
		Delete co
End Function

Function reportcop()
For co.cop=Each cop
	info("Cop at ("+EntityX(co\copbody)+","+EntityY(co\copbody)+","+EntityZ(co\copbody)+")")
Next
End Function

Function reportbot()
For co.bot=Each bot
	info("Bot at ("+EntityX(co\bbody)+","+EntityY(co\bbody)+","+EntityZ(co\bbody)+")")
Next
End Function

Function reportUFO()
For co.UFO=Each UFO
	info("UFO at "+EntityX(co\copbody)+","+EntityY(co\copbody)+","+EntityZ(co\copbody)+")")
Next
End Function

;make bullet
Function CreateBullet.Bullet( entity,typemode,down )
	bull_x=-bull_x
	bul.Bullet=New Bullet
	bul\time_out=150
	bul\sprite=LoadSprite( "BLUSPARK.BMP" )
	PositionEntity bul\sprite,EntityX(entity),EntityY(entity),EntityZ(entity)
	RotateEntity bul\sprite,EntityPitch(entity)+down,EntityYaw(entity),EntityZ(entity)
	TranslateEntity bul\sprite,0,1,0
	ScaleSprite bul\sprite,1,1
	EntityRadius bul\sprite,1
	EntityType bul\sprite,typemode
	EntityAlpha bul\sprite,1
	EmitSound (sfxshoot,entity)
	Return bul
End Function

;update bullet stuff
Function UpdateBullet.player( bul.Bullet )
	If CountCollisions( bul\sprite )	
			For k=1 To CountCollisions( bul\sprite )
				If GetEntityType( CollisionEntity( bul\sprite,k ) )=SCENE
					cx#=CollisionX( bul\sprite,k )
					cy#=CollisionY( bul\sprite,k )
					cz#=CollisionZ( bul\sprite,k )
					nx#=CollisionNX( bul\sprite,k )
					ny#=CollisionNY( bul\sprite,k )
					nz#=CollisionNZ( bul\sprite,k )
					Exit
				EndIf
				If GetEntityType( CollisionEntity( bul\sprite,k ) )=POLICE
					cx#=CollisionX( bul\sprite,k )
					cy#=CollisionY( bul\sprite,k )
					cz#=CollisionZ( bul\sprite,k )
					nx#=CollisionNX( bul\sprite,k )
					ny#=CollisionNY( bul\sprite,k )
					nz#=CollisionNZ( bul\sprite,k )
					Exit
				EndIf
				If GetEntityType( CollisionEntity( bul\sprite,k ) )=Type_BOT
					cx#=CollisionX( bul\sprite,k )
					cy#=CollisionY( bul\sprite,k )
					cz#=CollisionZ( bul\sprite,k )
					nx#=CollisionNX( bul\sprite,k )
					ny#=CollisionNY( bul\sprite,k )
					nz#=CollisionNZ( bul\sprite,k )
					Exit
				EndIf
				If GetEntityType( CollisionEntity( bul\sprite,k ) )=NET
					cx#=CollisionX( bul\sprite,k )
					cy#=CollisionY( bul\sprite,k )
					cz#=CollisionZ( bul\sprite,k )
					nx#=CollisionNX( bul\sprite,k )
					ny#=CollisionNY( bul\sprite,k )
					nz#=CollisionNZ( bul\sprite,k )
					Exit
				EndIf
				If GetEntityType( CollisionEntity( bul\sprite,k ) )=TYPE_GRASS
					cx#=CollisionX( bul\sprite,k )
					cy#=CollisionY( bul\sprite,k )
					cz#=CollisionZ( bul\sprite,k )
					nx#=CollisionNX( bul\sprite,k )
					ny#=CollisionNY( bul\sprite,k )
					nz#=CollisionNZ( bul\sprite,k )
					Exit
				EndIf
				If GetEntityType( CollisionEntity( bul\sprite,k ) )=BODY
				If god=0 Then
					hits=hits+1
					info("Laser damage: "+hits*10+"%")
				EndIf
					cx#=CollisionX( bul\sprite,k )
					cy#=CollisionY( bul\sprite,k )
					cz#=CollisionZ( bul\sprite,k )
					nx#=CollisionNX( bul\sprite,k )
					ny#=CollisionNY( bul\sprite,k )
					nz#=CollisionNZ( bul\sprite,k )
					Exit
				EndIf
			Next
			CreateSpark( bul\sprite )
			FreeEntity bul\sprite
			Delete bul
			Return
		EndIf
	bul\time_out=bul\time_out-1
	If bul\time_out=0
		FreeEntity bul\sprite
		Delete bul
		Return
	EndIf
	bul\rot=bul\rot+30
	RotateSprite bul\sprite,bul\rot
	MoveEntity bul\sprite,0,0,3
End Function

;create explosion
Function CreateSpark.Spark( entity )
	spark_sprite=LoadSprite( "Bigspark.BMP" )
	HideEntity spark_sprite
	EmitSound(sfxboom,entity)
	s.Spark=New Spark
	s\alpha=-90
	s\sprite=CopyEntity( spark_sprite,entity )
	EntityParent s\sprite,0
	Return s
End Function

;update explosion
Function UpdateSpark( s.Spark )
	If s\alpha<270
		sz#=Sin(s\alpha)*5+5
		ScaleSprite s\sprite,sz,sz
		RotateSprite s\sprite,Rnd(360)
		s\alpha=s\alpha+15
	Else
		FreeEntity s\sprite
		Delete s
	EndIf
End Function

;hole update?

Function writesettings()
	information=WriteFile("game.inf")
	WriteLine(information,name$)
	WriteLine(information,filename$)
	WriteLine(information,mode#)
	WriteLine(information,control#)
	WriteLine(information,traxlist$)
	CloseFile(information)
End Function

Function UFOcontrol(entity,netstatus$,PlayerID)
	reload=reload+1

	If JoyDown(6) And reload>5 And laser=True Then
		CreateBullet(entity,TYPE_BULLET,0)
		If netstatus$="On" Or netstatus$="Net" Then
			SendNetMsg 4, "BLASTED!",PlayerID,0
		EndIf
		reload=0
	EndIf
	
	If JoyDown(1) And reload>5 And laser=True Then
		If Not netstatus$="On" Then
		If Not netstatus$="Net" Then
			CreateBullet(entity,TYPE_BULLET,90)
			reload=0
		EndIf
		EndIf
	EndIf

	turn#=JoyX()*TurnFactor#
	
	RotateEntity wheels[1],0,-turn#*10,0,False
	RotateEntity wheels[2],0,-turn#*10,0,False
	
	If JoyHit(4) Then
	RotateEntity entity,0,0,0
	EndIf
		
	;move car
	StopChannel(SOUND)
	
	If speed>0.01 Then
	SoundPitch Engine,ESpeed*5000
				If Not ChannelPlaying(SOUND)
					SOUND=PlaySound(engine)
				EndIf
	EndIf
	If speed<0.01 Then
	SoundPitch Engine,-ESpeed*10000
				If Not ChannelPlaying(SOUND)
					SOUND=PlaySound(engine)
				EndIf
	EndIf

			Espeed#=Espeed+(Accel#*(-JoyY()))
			If Espeed>Maxspeed# Then Espeed#=Maxspeed#
			If Espeed<-0.5 Espeed#=-0.5
			If JoyY()>-.1 And JoyY()<.1 Then
			Espeed#=Espeed-.02
			If Espeed<0 Then Espeed#=0
			EndIf
			
	If JoyDown(7) Then
	MoveEntity entity,0,-maxspeed#,0
	Else If JoyDown(8) Then
	MoveEntity entity,0,maxspeed#,0
	EndIf
	
	
;	If EntityCollided( wheels[3],SCENE ) And EntityCollided( wheels[4],SCENE )
		TurnEntity entity,0,-turn,0
		
		speed#=Espeed
		MoveEntity entity,0,0,speed#
;		TranslateEntity entity,0,GRAVITY,0
;	Else
;		TranslateEntity entity,x_vel,y_vel+GRAVITY,z_vel
;	EndIf
	
End Function

Function joycontrol(entity,netstatus$,PlayerID)
	reload=reload+1

	If JoyDown(1) And reload>5 And laser=True Then
		CreateBullet(Car,TYPE_BULLET,0)
		If netstatus$="On" Or netstatus$="Net" Then
			SendNetMsg 4, "BLASTED!",PlayerID,0
		EndIf
		reload=0
	EndIf

	turn#=JoyX()*TurnFactor#
	
	RotateEntity wheels[1],0,-turn#*10,0,False
	RotateEntity wheels[2],0,-turn#*10,0,False
	
	;move car
	StopChannel(SOUND)
	
	If speed>0.01 Then
	SoundPitch Engine,ESpeed*5000
				If Not ChannelPlaying(SOUND)
					SOUND=PlaySound(engine)
				EndIf
	EndIf
	If speed<0.01 Then
	SoundPitch Engine,-ESpeed*10000
				If Not ChannelPlaying(SOUND)
					SOUND=PlaySound(engine)
				EndIf
	EndIf

			Espeed#=Espeed+(Accel#*(-JoyY()))
			If Espeed>Maxspeed# Then Espeed#=Maxspeed#
			If Espeed<-0.5 Espeed#=-0.5
			If JoyY()>-.1 And JoyY()<.1 Then
			Espeed#=Espeed-.02
			If Espeed<0 Then Espeed#=0
			EndIf
			
	If EntityCollided( wheels[3],SCENE ) And EntityCollided( wheels[4],SCENE )
		If speed#>0.1 Then TurnEntity entity,0,-turn,0
		If speed#<-0.1 Then TurnEntity entity,0,turn,0
		
		speed#=Espeed
		MoveEntity entity,0,0,speed#
		TranslateEntity entity,0,GRAVITY,0
	Else
		TranslateEntity entity,x_vel,y_vel+GRAVITY,z_vel
	EndIf
	
End Function

Function FlightStick(entity,netstatus$,PlayerID)
reload=reload+1
	If JoyDown(1) And reload>5 And laser=True Then
		CreateBullet(Car,TYPE_BULLET,0)
		If netstatus$="On" Or netstatus$="Net" Then
			SendNetMsg 4, "BLASTED!",PlayerID,0
		EndIf
		reload=0
	EndIf
	
	Speed=((-JoyZ()+1)/2)*(Maxspeed#)
	
	TurnEntity entity,-JoyY()*TurnFactor#,(-JoyRoll()/180)*TurnFactor#,-JoyX()*TurnFactor#
	MoveEntity entity,0,0,((-JoyZ()+1)/2)*(Maxspeed#)
	If ((-JoyZ()+1)/2)=0 Then
		If JoyDown(2) Then
			MoveEntity entity,0,0,-0.5
		EndIf
		TranslateEntity entity,0,GRAVITY*10,0
	EndIf
	falldam=0
End Function

Function keyboardcontrol(entity,netstatus$,PlayerID)
	reload=reload+1

	If KeyDown(29) Or KeyDown(157)
	If armed=True And reload>5 And laser=True Then
		If netstatus$="On" Or netstatus$="Net" Then
			SendNetMsg 4, "BLASTED!",PlayerID,0
		EndIf
		CreateBullet(entity,TYPE_BULLET,0)
		reload=0
	EndIf
	EndIf

	;move car
	StopChannel(SOUND)
	
	If speed>0.01 Then
	SoundPitch Engine,ESpeed*5000
				If Not ChannelPlaying(SOUND)
					SOUND=PlaySound(engine)
				EndIf
	EndIf
	If speed<0.01 Then
	SoundPitch Engine,-ESpeed*10000
				If Not ChannelPlaying(SOUND)
					SOUND=PlaySound(engine)
				EndIf
	EndIf

		If KeyDown(200)
			Espeed#=Espeed#+Accel#
			
			If KeyDown(208) Then Espeed#=Espeed-Accel#*2
			
			If Espeed#>Maxspeed# Espeed#=Maxspeed#
			
		Else If KeyDown(208)
			If Espeed#>0 Then
				Espeed#=Espeed-Accel#*10
			Else
				Espeed#=Espeed-Accel#
			EndIf
			If Espeed<-0.5 Then Espeed#=-0.5
		Else
		Espeed#=Espeed-.02
		If Espeed<0 Then Espeed#=0
		EndIf
	
		TranslateEntity entity,0,GRAVITY,0,True
	If EntityCollided( wheels[3],SCENE ) And EntityCollided( wheels[4],SCENE )
		speed#=Espeed#
		MoveEntity entity,0,0,speed
		TurnEntity wheels[3],speed#*60,0,0
		TurnEntity wheels[4],speed#*60,0,0

			If speed#>0.1
					If KeyDown(203) Then
					TurnEntity entity,0,turnfactor#,0
					RotateEntity wheels[1],0,turnfactor#*10,0
					RotateEntity wheels[2],0,turnfactor#*10,0
					speed#=speed#-(turn#/1000)
					Else If KeyDown(205) Then
					RotateEntity wheels[1],0,-turnfactor#*10,0
					RotateEntity wheels[2],0,-turnfactor#*10,0
					TurnEntity entity,0,-turnfactor#,0
					speed#=speed#-(turn#/1000)
					Else
					RotateEntity wheels[1],0,0,0,False
					RotateEntity wheels[2],0,0,0,False
					EndIf
			Else
				If speed#<-0.1
					If KeyDown(203) Then
					TurnEntity entity,0,-turnfactor#,0
					RotateEntity wheels[1],0,turnfactor#*10,0
					RotateEntity wheels[2],0,turnfactor#*10,0
					Else If KeyDown(205) Then
					TurnEntity entity,0,turnfactor#,0
					RotateEntity wheels[1],0,-turnfactor#*10,0
					RotateEntity wheels[2],0,-turnfactor#*10,0				
					Else
					RotateEntity wheels[1],0,0,0,False
					RotateEntity wheels[2],0,0,0,False
					EndIf
				EndIf
			EndIf
	Else
					If KeyDown(203) Then
					RotateEntity wheels[1],0,turnfactor#*10,0,False
					RotateEntity wheels[2],0,turnfactor#*10,0,False
					Else If KeyDown(205) Then
					RotateEntity wheels[1],0,-turnfactor#*10,0
					RotateEntity wheels[2],0,-turnfactor#*10,0
					Else
					RotateEntity wheels[1],0,0,0,False
					RotateEntity wheels[2],0,0,0,False
					EndIf
		TranslateEntity entity,x_vel,y_vel,z_vel ;+GRAVITY
	EndIf
	

End Function

Function mousecontrol(entity,netstatus$,PlayerID)

	reload=reload+1

	If MouseDown(3) And reload>5 And laser=True Then
		CreateBullet(entity,TYPE_BULLET,0)
		If netstatus$="On" Or netstatus$="Net" Then
			SendNetMsg 4, "BLASTED!",PlayerID,0
		EndIf
		reload=0
	EndIf

	; Mouse x and y speed
	mxs#=MouseXSpeed()
	mys#=MouseYSpeed()

	mx#=MouseX()
	
	If mxs#>TurnFactor# Then mxs#=TurnFactor#
	If mxs#<-TurnFactor# Then mxs#=-TurnFactor#
	
	RotateEntity wheels[1],0,-mxs#*10,0,False
	RotateEntity wheels[2],0,-mxs#*10,0,False

	; Rest mouse position to centre of screen

	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

	;move car
	StopChannel(SOUND)
	
	If Espeed>0.01 Then
	SoundPitch Engine,ESpeed*5000
				If Not ChannelPlaying(SOUND)
					SOUND=PlaySound(engine)
				EndIf
	EndIf
	If Espeed<0.01 Then
	SoundPitch Engine,-ESpeed*10000
				If Not ChannelPlaying(SOUND)
					SOUND=PlaySound(engine)
				EndIf
	EndIf
	

		If MouseDown(1)
			Espeed#=Espeed+Accel#
			If Espeed>Maxspeed# Espeed#=Maxspeed#
		Else If MouseDown(2)
			Espeed#=Espeed-Accel#*2
			If Espeed<-0.5 Then Espeed#=-0.5
		Else
		Espeed#=Espeed-.02
		If Espeed<0 Then Espeed#=0
		EndIf

		
	
	TranslateEntity entity,0,GRAVITY,0
	If EntityCollided( wheels[3],SCENE ) And EntityCollided( wheels[4],SCENE )
		If speed>0.1 Then TurnEntity entity,0,-mxs#,0
		If speed<-0.1 Then TurnEntity entity,0,mxs#,0
		
		speed#=Espeed
		MoveEntity entity,0,0,speed
	Else
		TranslateEntity entity,x_vel,y_vel,z_vel ;+GRAVITY,
	EndIf

End Function

Function createstealth.npc()
	np.npc = New npc
		np\mesh=LoadMesh("stealth.3ds")
		ScaleEntity np\mesh,.05,.05,.05
		PositionEntity np\mesh,0,200,250
		clone=CopyEntity(np\mesh,np\mesh)
		PositionEntity clone,EntityX(np\mesh),.1,EntityZ(np\mesh),1
		ScaleEntity clone,1,.01,1
		EntityColor clone,0,0,0
		EntityAlpha clone,.5
End Function

Function createsaucer()
	np.npc = New npc
		np\mesh=LoadMesh("	fsaucer1.3ds")
;		ScaleEntity np\mesh,.5,.5,.5
		PositionEntity np\mesh,EntityX(car)+Rand(-100,100),200,EntityZ(car)+Rand(-100,100)
		clone=CopyEntity(np\mesh,np\mesh)
		PositionEntity clone,EntityX(np\mesh),.1,EntityZ(np\mesh),1
		ScaleEntity clone,1,.01,1
		EntityColor clone,0,0,0
		EntityAlpha clone,.5
End Function

Function updatenpc.npc(np.npc)
	TurnEntity np\mesh,0,.1,0
	MoveEntity np\mesh,0,0,-1
	If Not ChannelPlaying(np\CHANNEL)
		np\CHANNEL=EmitSound(SFXSUCTION,np\mesh)
	EndIf		
End Function

Function killnpc.npc(np.npc)
	HideEntity np\mesh
	Delete np
End Function

Function cheatlist(thecheat$)
cheat$=Lower(thecheat$)
	Select cheat$
	
	Case "wanted":
	createcop()
			
	Case "solid car":
	EntityAlpha car,1

	Case "no car"
	EntityAlpha car,0

	Case "transparent car"
	EntityAlpha car,.5
	
	Case "heal me"
	hits=0

	Case "transparent wheels"
	EntityAlpha wheels[1],.5
	EntityAlpha wheels[2],.5
	EntityAlpha wheels[3],.5
	EntityAlpha wheels[4],.5

	Case "solid wheels"
	EntityAlpha wheels[1],1
	EntityAlpha wheels[2],1
	EntityAlpha wheels[3],1
	EntityAlpha wheels[4],1
				
	Case "no wheels"
	EntityAlpha wheels[1],0
	EntityAlpha wheels[2],0
	EntityAlpha wheels[3],0
	EntityAlpha wheels[4],0
	
	Case "smashproof"
	If falldam=1 Then
	falldam=0
	Else
	falldam=1
	EndIf
	info("Gravity pain turned to value "+Int(falldam))
	
	Case "transparent walls"
	For count=-6 To 6
		EntityAlpha copy[count+6],.5
		EntityAlpha copy[count+94],.5
	Next
	
	Case "solid walls"
	For count=-6 To 6
		EntityAlpha copy[count+6],1
		EntityAlpha copy[count+94],1
	Next
	
	Case "no walls"
	For count=-6 To 6
		EntityAlpha copy[count+6],0
		EntityAlpha copy[count+94],0
	Next
	
	Case "god"
	If god=1 Then
	god=0
	info("God mode off")
	Else
	god=1
	info("God mode on")
	EndIf
		
	Case "wireframe true"
	WireFrame True
	
	Case "wireframe false"
	WireFrame False

	Default:
	info("Cheat failed!")
	End Select
End Function

Function colorlist(thecheat$)
cheat$=Lower(thecheat$)
	Select cheat$
		
		Case "blue":
		EntityColor car,0,0,255
		cartoneA#=0
		cartoneB#=0
		cartoneC#=255
	
		Case "green":
		EntityColor car,0,255,0
		cartoneA#=0
		cartoneB#=255
		cartoneC#=0
		
		Case "red":
		EntityColor car,255,0,0
		cartoneA#=255
		cartoneB#=0
		cartoneC#=0		
		
		Case "black":
		EntityColor car,0,0,0
		cartoneA#=0
		cartoneB#=0
		cartoneC#=0
	
		Case "white":
		EntityColor car,255,255,255
		cartoneA#=255
		cartoneB#=255
		cartoneC#=255		
		
		Case "light blue":
		EntityColor car,0,255,255
		cartoneA#=0
		cartoneB#=255
		cartoneC#=255		
		
		Case "pink":
		EntityColor car,255,0,255
		cartoneA#=255
		cartoneB#=0
		cartoneC#=255		
				
		Case "yellow":
		EntityColor car,255,255,0
		cartoneA#=255
		cartoneB#=255
		cartoneC#=0
				
		Default
		info("color unknown, defaulting...")
		EntityColor car,0,0,0
		cartoneA#=0
		cartoneB#=0
		cartoneC#=0
		
	End Select 
End Function


Function info( t$ )
	inf.Info=New Info
	inf\txt$=t$
	Insert inf Before First Info
End Function

Function command(CMString$,Value$)
CString$=Lower(CMString$)
If CString$="cheat" Then
info("You cheated with the code "+value$)
			If netstatus$="On" Or netstatus$="Net" Then
				SendNetMsg 9,value$,PlayerID,0
			EndIf
				cheatlist(value$)
				
Else If CString$="say" Then
	If netstatus$="On" Or netstatus$="Net" Then
		SendNetMsg 2,Value$,PlayerID,0,0
	EndIf
		info("You Said: "+Value$)
		
Else If CString$="echo" Then
	info(value$)

Else If CString$="flush" Then
	If Lower(value$)="toilet" Then
		info("System: OH CRAP!!!")
		info("***FLUSH***")
		info("Toilet: gurgle")
	Else If Lower(value$)="log" Then
		flushinfo()
	Else
		info("Can't flush "+value$+".")
	EndIf

Else If CString$="engine" Then
	SoundVolume Engine,value

Else If CString$="mvol" Then
	ChannelVolume bgm,value

Else If CString$="track" Then
	StopChannel(bgm)
	track=value
	track=track-1

Else If CString$="traxlist" Then
	StopChannel(bgm)
	traxlist$=value$
	info("Will load when you start the program next time.")
	
Else If CString$="respawncop" Then
	respawncop=value

Else If CString$="color" Then
	colorlist(value$)
	info("Changed car color to "+value$+".")
Else If CString$="gravity" Then
	gravity=value
	info("Gravity changed from default value -.06 to "+gravity)

Else If CString$="" Then
tvalue$=Lower(value$)
	If tvalue$="fix" Then
		info("Car spun.")
		RotateEntity car,0,0,0
		
	Else If tvalue$="respawn" Then
		info("Car re-dropped.")
		PositionEntity car,0,0,0,True
	
	Else If tvalue$="fluffy" Then
		info("Yes, Tim farted on this too.")
	
	Else If tvalue$="wheels" Then
			EntityType wheels[1],99
			EntityType wheels[2],99
			EntityType wheels[3],99
			EntityType wheels[4],99
				;resposition wheels
				cnt=1
				For z=1.5 To -1.5 Step -3
				For x=-1 To 1 Step 2
					PositionEntity wheels[cnt],x,-1,z
					cnt=cnt+1
				Next
				Next
				
			EntityType wheels[1],WHEEL
			EntityType wheels[2],WHEEL
			EntityType wheels[3],WHEEL
			EntityType wheels[4],WHEEL
		info("Wheels fixed.")
		PositionEntity car,0,0,0,True
	
	Else If tvalue$="addcop" Then
		createcop()
		info("Cop added to game.")
	
	Else If tvalue$="report" Then
		info("Reporting...")
		reportcop()		
		reportbot()
		info("End of report")
	
	Else If tvalue$="killcop" Then
		hidecop(Last cop)
		info("A cop was removed.")
	
	Else If tvalue$="screenshot" Then
		scrshot()
		
	Else If tvalue$="killufo" Then
		hideUFO(Last UFO)
		info("A UFO was removed.")
	
	Else If tvalue$="addbot" Then
		createbot()
		info("Bot added to game.")
	
	Else If tvalue$="killbot" Then
		HideRBot(Last bot)
		info("A bot was removed.")
	
	Else If tvalue$="cls" Then
		flushinfo()
		
	Else If tvalue$="addufo" Then
		createufo()
		info("They have landed.")
			
	Else If tvalue$="addstealth" Then
		createstealth()
		info("The millitary is here.")
			
	Else If tvalue$="addsaucer" Then
		createsaucer()
		info("They have arrived.")

	Else If tvalue$="stats" Then
		info("Damage: "+Int(hits*10)+"%")

	Else If tvalue$="log" Then
		savelog()
		
	Else
	info("Value "+value$+" unrecognized")
	EndIf
	
Else
	info("Command "+CMString$+"="+value$+" unrecognized.")
EndIf
	
End Function

Function flushinfo()
For inf.info = Each info
Delete inf
Next
info("Log flushed.")
End Function

Function savelog()
		file=WriteFile("log.txt")
		WriteLine(file,"Name="+name$)
		WriteLine(file,"gamesave="+filename$)
		WriteLine(file,"mode#="+mode#)
		WriteLine(file,"control#="+control#)
		WriteLine(file,"Renegade log file")
		WriteLine(file,"Log created at "+CurrentTime()+" on "+CurrentDate())
		WriteLine(file,"--------------End of log--------------")
		For inf.Info=Each Info
				WriteLine(file,inf\txt$)
		Next
		WriteLine(file,"-------------Start of log-----------------")
		CloseFile file
		info("Log file saved.")
End Function

Function BuildTracklist()

If FileType(traxlist$)=1

	fhandle=ReadFile(traxlist$)
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
					Case "tracks"
					parse=After parse
					Repeat
						m.track =New track
						sep=Instr(parse\sdat,"=")
						del1$=Mid$(parse\sdat,6,sep-6)
						m\number#=del1
						m\loadname$=Right$(parse\sdat,Len(parse\sdat)-sep)
						parse=After parse
						If m\number#>maxtrax# Then
							maxtrax#=m\number#
						EndIf
				Until parse\sdat=")"			

				End Select
			EndIf
			parse=After parse
		Wend
	Else
		info("Script File "+Chr$(34)+traxlist$+Chr$(34)+" is not readable.")
		savelog()
		RuntimeError"Script File "+Chr$(34)+traxlist$+Chr$(34)+" is not readable."
	EndIf

Else
	info("Script File "+Chr$(34)+traxlist$+Chr$(34)+" is not a file.")
	savelog()
	RuntimeError"Script File "+Chr$(34)+traxlist$+Chr$(34)+" is not a file."
EndIf	

End Function

Function LoadAddonCars()

If FileType("addoncars.txt")=1

	fhandle=ReadFile("addoncars.txt")
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
		info("Script File "+Chr$(34)+"addoncars.txt"+Chr$(34)+" is not readable.")
		savelog()
		RuntimeError"Script File "+Chr$(34)+"addoncars.txt"+Chr$(34)+" is not readable."
	EndIf

Else
	info("Script File "+Chr$(34)+"addoncars.txt"+Chr$(34)+" is not a file.")
	savelog()
	RuntimeError"Script File "+Chr$(34)+"addoncars.txt"+Chr$(34)+" is not a file."
EndIf	

For spt.scriptentry = Each scriptentry
	Delete spt
Next 

End Function


Function ScrShot()
SaveBuffer(BackBuffer(),"screenshot.bmp")
info("Saved screen shot screenshot.bmp")
End Function