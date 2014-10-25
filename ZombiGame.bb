;;;;;; ZOMBI GAME ;;;;;;;
;;  Include "func.bb" ;;;     
;;;;;;;;;;;;;;;;;;;;;;;;;

Type Enemy
    Field  status
    Field  model
    Field  pivot_enemy
    Field  speed_enemy#
End Type

Type spark
    Field entity,alpha#
End Type

Type blood
    Field entity,alpha#,y_speed#,stuck
End Type

Dim aEnemy.Enemy (max)
Dim aWalk(2)

Const max = 20
Global num_enemy = 0
Const  alive = 1, die = 0 ,reborn = 2
Global god = False
Global timer = 0
Const shootDist = 60
Const  typePlayer = 1 , typeWall = 2 , typeEnemy  = 3 , typeWall_player = 4 ,type_camera = 5, type_blood = 6
Const width = 800
Const height = 600
Const player_Y = 14
Const speed_player# = 0.6   
Global lModeShoot = False
Global picked
Global lPlayerAlive = True
Global GAME_OVER = False
Global SCORE = 0
Global lWalking 
Global hearth = 0
Global pl_d
Global iSoundWalk = 0, steptick
Global alpha_sp = 0
Global bb# , gg#
Global LastPlayerPos
Global blood_intensity = 20 , mouse_shake#  
Global cons_flag = False
;;;;;;;;;;;;;;;;;;;;
SeedRnd MilliSecs()
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Graphics3D width ,height ,32, 1            ;;;;;;;   SCREEN MODE     ;;;;;;;;;
SetBuffer BackBuffer()
HidePointer()
AppTitle "L.A.S.T.D.A.Y. by ar[]ne v.1.0"
fntArial=LoadFont("Arial",34) 
SetFont fntArial
fntCourier = LoadFont("Courier",20)


; ----- CAMERA  ----
Global player = CreatePivot()
EntityRadius player , 10
EntityType player,typePlayer               ; Collision
PositionEntity player , 0 , player_Y , 0



Global cam = CreateCamera(player )
CameraRange cam,1,140
CameraFogMode cam,1                            ; ----  FOG   ---
CameraFogRange cam,60,120
CameraFogColor cam,10,10,10
EntityType cam,type_camera
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Global spark=LoadSprite("media/bigspark.bmp")   
ScaleSprite spark,1,1
EntityOrder spark,-1
HideEntity spark
;;;;;;;;;;;;;;;;;;;;;;

micro = CreateListener (cam,0.1)
Global  new_game_sound1 = LoadSound("sound/new_game.wav")
Global  new_game_sound2 = LoadSound ("sound/te_death2.wav")

Global  die1 = Load3DSound ("sound/con_die1.wav")
Global  die2 = Load3DSound ("sound/con_die2.wav")

pl_die1 = LoadSound("sound/scream1.wav")
 pl_die2 = LoadSound("sound/scream2.wav")

heartbeat = LoadSound("sound/heartbeat1.wav")

gun_shoot_sound1 = LoadSound("sound/gun_shot.wav")
 gun_shoot_sound2 = LoadSound("sound/biggun2.wav")

aWalk(0) = LoadSound("sound/walk1.wav") 
aWalk(1) = LoadSound("sound/walk2.wav") 

;;;;;;;;;;;;;;;;;
; --  LIGHT ---
;;;;;;;;;;;;;;;;
;AmbientLight 255,255,255
AmbientLight 110,110,110

;---- MAP ----

Global map = LoadMesh ("level/map_1.b3d")
EntityType map, typeWall
ScaleEntity map, 0.05,0.05,0.05
EntityPickMode map, 2 
;--------------------
; ----- ENEMY -------
Global e_tex1 = LoadTexture ("media/Archvile/archvile.png")

Global e_die =LoadMD2("media/Archvile/Archdie.md2")
Global e_go = LoadMD2 ("media/Archvile/Archvile.md2")

PositionEntity e_die,0,1,0
PositionEntity e_go,0,1,0
EntityPickMode e_go , 2


ScaleEntity e_die,0.08,0.08,0.08
ScaleEntity e_go,0.08,0.08,0.08

EntityTexture e_die,e_tex1
EntityTexture e_go,e_tex1
RotateEntity e_go,0,180,0
RotateEntity e_die,0,180,0

HideEntity e_die
HideEntity e_go
;;;;;;;;;;;;;;;;;;;;;;;;;;

Global blood=LoadSprite("media/blood.bmp",3)
EntityRadius blood,1
EntityType blood,type_blood
HideEntity blood


; - Cursor ---
cur = LoadImage("media/cross5.bmp")
died_fon = LoadImage("media/died_fon.jpg")

;;;;;;;;;;;;;;;;;;;;;;;

Collisions typePlayer , typeWall  , 2 , 2
Collisions typePlayer , typeEnemy , 2 , 1
Collisions typeEnemy  , typeWall  , 2 , 2
Collisions typeEnemy  , typeEnemy , 2 , 2
Collisions typePlayer , typeWall_player ,2,2
Collisions type_blood , type_camera, 1 , 1 
Collisions type_blood , typeWall ,   2 , 1


;;;;;;;;;;  Dopolnit  Walls   ;;;;;;;;;;;
w1 = CreateCube()
PositionEntity w1 , 0,0,350
ScaleEntity w1 ,80,20,1
EntityAlpha w1 ,0
EntityType w1 , typeWall_player
EntityPickMode w1 ,0

w2 = CreateCube()
PositionEntity w2 , 0,0,-350
ScaleEntity w2 ,80,20,1
EntityAlpha w2 ,0
EntityType w2 , typeWall_player
EntityPickMode w2 ,0

w3 = CreateCube()
PositionEntity w3 , 350,0,0
ScaleEntity w3 ,1,20,80
EntityAlpha w3 ,0
EntityType w3 , typeWall_player
EntityPickMode w3 ,0

w4 = CreateCube()
PositionEntity w4 , -350,0,0
ScaleEntity w4 ,1,20,80
EntityAlpha w4 ,0
EntityType w4 , typeWall_player
EntityPickMode w4 ,0

;;;;;;; LOAD WEAPON  ;;;;;;;;;;

    Global  weapon  = LoadAnimMesh("media/gun.B3D", cam)
    weap_tex = LoadTexture ("media/bga.bmp")
    EntityTexture weapon,weap_tex
    ExtractAnimSeq(weapon,1,4 )     ; 1-  idle
    ExtractAnimSeq(weapon,5,25 )    ; 2-  shoot 
    Animate weapon, 1, 0.8, 1
    EntityPickMode weapon, 2  
    PositionEntity weapon,11,6,-16
    
     RotateEntity weapon,0,180,0
     EntityParent weapon, cam
     EntityRadius weapon,1
     EntityOrder weapon,-1
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

LoadEnemy(max)  
new_game_sound()

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  ------ MAIN () -------- ;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
While Not KeyHit(1)

If  GAME_OVER = False
      lWalking = False
        
    If num_enemy < max 
        RebornEnemy()
    EndIf
    
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    enemyengine()
    SpurtBlood()
    UpdateGun()
    die_enemy()
    

;;;;;;;;;
;;;;;;;; MOUSE ACTIVITI!!! ;;;;;
    mxspd#=MouseXSpeed()*0.2
    myspd#=MouseYSpeed()*0.2
    
    MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

    campitch=campitch+myspd
    If campitch<-86 Then campitch= -86
    If campitch>86 Then campitch= 86
    RotateEntity player,campitch,EntityYaw(player)-mxspd,0
    

  If  lPlayerAlive
   If MouseDown(1)
        If Not lModeShoot
            Animate weapon, 1,  1.1 , 2
            a = Rnd(1,2)
            Select a
               Case 1
                  PlaySound gun_shoot_sound1
               Case 2
                  PlaySound gun_shoot_sound2
            End Select
            
            picked=CameraPick(cam,MouseX(),MouseY())
            s.spark=New spark
            s\entity=CopyEntity(spark)
            s\alpha=1
            PositionEntity s\entity,PickedX(),PickedY(),PickedZ()
    
        EndIf
        lModeShoot = True
   Else
        If lModeShoot
            Animate weapon, 1, 1.1, 1
            lModeShoot = False
        EndIf 
   EndIf

    If KeyHit(59) Then 
        If cons_flag = False
                cons_flag = True
        Else 
                cons_flag = False
        EndIf
    EndIf

 
    
    If KeyDown (200)=True Or KeyDown(17)=True Then 
        MoveEntity player,0,0,speed_player 
        lWalking = True
    EndIf
    If KeyDown (208)=True Or KeyDown(31)=True Then 
        MoveEntity player,0,0,-speed_player 
        lWalking = True
    EndIf
    If KeyDown (203)=True Or KeyDown(30)=True Then
         MoveEntity player,-speed_player/2,0,0 
         lWalking = True
    EndIf
    If KeyDown (205)=True Or KeyDown(32)=True Then
         MoveEntity player, speed_player/2,0,0 
         lWalking = True
     EndIf
    
     If lWalking
        StepsSound()
     EndIf 
    
    If EntityY(player) <> player_Y Then
        PositionEntity player,EntityX(player),player_Y,EntityZ(player)
    EndIf

    If hearth < MilliSecs()
        PlaySound heartbeat
        hearth = MilliSecs() + 600
    EndIf


  Else 
     sleep()
    
        If pl_d
            rd = Rnd(1,2)
            Select rd
                Case 1
                    PlaySound pl_die1
                Case 2
                    PlaySound pl_die2
            End Select
            pl_d = False
        EndIf
EndIf
    

    DrawImage( cur ,GraphicsWidth()/2-16, GraphicsHeight()/2-25 )

 If cons_flag = True Then
    ; FPS
    frames=frames+1
    If MilliSecs()-render_time=>1000 Then fps=frames : frames=0 : render_time=MilliSecs()
    Color 255,255,255   
    Text 0,0,"FPS: "+fps
;   Text 0,20,"PickedEntity: "+PickedEntity() 
    ;Text 0,40,"NumEnemy: "+ num_enemy
;   Text 0,60,"max: "+ max
    ;Text 0,80,"Number picked: "+ picked
    Text 0,25, "Score: " +SCORE

 EndIf



Else 
    Cls
    Color 55,0,255 
    DrawImage died_fon,0,0
    Text width/2-90,height/2-90,"GAME OVER"
    Color 0,0,0 
    Text width/2-140,height/2-20,"Press Enter Or Space"
    Text width/2-105,height/2+20, "YOUR SCORE :" + SCORE
    Color 0,90,155
    SetFont fntCourier
    Text width/2-75,height/2+230, "coding by ar[]ne"
    Text width/2-92,height/2+250, "http://vk.com/arone "
    SetFont fntArial
    
    If KeyHit(57) Or KeyHit(28) Then
        For i=1 To max
            aEnemy(i)\speed_enemy# = 0.3 + Rnd#(0 , 0.3)
            aEnemy(i)\status = die
        Next
            new_game_sound()
            PositionEntity player , 0 , player_Y , 0
            AmbientLight 120,120,120
            SCORE = 0
            lPlayerAlive = True
            GAME_OVER = False
      EndIf
    
 EndIf

;;;;;;;;;;;;;;;;
   Flip
    UpdateWorld
    RenderWorld
;;;;;;;;;;;;;;;

Wend
    FreeEntity map
    FreeEntity weapon
    FreeEntity e_go
    FreeEntity e_die
    FreeEntity spark
    FreeFont fntArial
    FreeTexture e_tex1
    FreeTexture weap_tex
    FreeEntity micro 
    FreeFont fntCourier

End
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;; FUNCTION ;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;
Function enemyengine()

 LastPlayerPos = CreatePivot ( player ) 

For i=1 To max
    If aEnemy(i)\status = alive Then
 
       If picked = aEnemy(i)\pivot_enemy  
    
         If EntityDistance(aEnemy(i)\pivot_enemy , player) <= shootDist
                FreshBlood()
                die_sound(aEnemy(i)\pivot_enemy)
                EntityType aEnemy(i)\pivot_enemy , 66
                EntityPickMode aEnemy(i)\pivot_enemy,0 
                FreeEntity aEnemy(i)\model
                picked = 0
                aEnemy(i)\status = die
                aEnemy(i)\model = CopyEntity( e_die , aEnemy(i)\pivot_enemy )
                PositionEntity aEnemy(i)\model , 0 ,-3 , 0
                AnimateMD2 aEnemy(i)\model,1,0.05
                timer = MilliSecs() 
                SCORE = SCORE + 1       
        EndIf
        
   EndIf
            
            
  If aEnemy(i)\status = alive   Then 
        If EntityCollided( aEnemy(i)\pivot_enemy, typeEnemy)
            rot_angle = Rand(-180, 180)  
            RotateEntity aEnemy(i)\pivot_enemy, 0, EntityYaw(aEnemy(i)\pivot_enemy)-rot_angle, EntityRoll(aEnemy(i)\pivot_enemy)
        EndIf
    
    If EntityY(aEnemy(i)\pivot_enemy) <> player_Y Then
        PositionEntity aEnemy(i)\pivot_enemy,EntityX(aEnemy(i)\pivot_enemy),player_Y,EntityZ(aEnemy(i)\pivot_enemy)
    EndIf
    
    If EntityCollided ( player , typeEnemy ) Then 
        lPlayerAlive = False
        god = True
        pl_d = True
    EndIf
    
    
         PointEntity   aEnemy(i)\pivot_enemy, LastPlayerPos
         RotateEntity  aEnemy(i)\pivot_enemy, 0, EntityYaw(aEnemy(i)\pivot_enemy)-180, EntityRoll(aEnemy(i)\pivot_enemy)
         MoveEntity    aEnemy(i)\pivot_enemy, 0, 0,-aEnemy(i)\speed_enemy#
    EndIf
  EndIf 

Next

End Function
;;;;;;;;

Function LoadEnemy(max)
    For i=1 To max
        num_enemy = num_enemy + 1
        poz = Rand(1,4)
        Select poz
            Case 1
                x = 40 +   Rand(-25, 25)
                z = 500 +  Rand (-100 , 200 )
            Case 2
                x = -20 +  Rand(-20,20)  
                z = -400 + Rand (-200 , 50 )
            Case 3
                x = 600 +  Rand(-100, 100)
                z = Rand(0,10)
            Case 4
                x = -600 + Rand(-100, 100)
                z = -23 + Rand(-20,20) 
        End Select
        
        aEnemy(i) = New Enemy
        aEnemy(i)\pivot_enemy = CreateSphere()
        ScaleEntity aEnemy(i)\pivot_enemy , 4,4,4
        
        bb# =  Rnd(0 ,  0.03) 
        gg# =  Rnd (0 , 0.2)
        
        EntityAlpha aEnemy(i)\pivot_enemy , alpha_sp
        aEnemy(i)\model= CopyEntity (e_go  , aEnemy(i)\pivot_enemy )
    
        PositionEntity aEnemy(i)\pivot_enemy , x ,  player_Y , z
        PositionEntity aEnemy(i)\model       , 0 , -3 , 0
        
        EntityType aEnemy(i)\pivot_enemy , typeEnemy
        EntityRadius aEnemy(i)\pivot_enemy, 8
        
        AnimateMD2 aEnemy(i)\model,1,0.06 + bb#
        EntityPickMode aEnemy(i)\pivot_enemy, 2 
        
        aEnemy(i)\speed_enemy# = 0.3 + gg#
        aEnemy(i)\status = alive    
    Next
End Function
;;;;;;;;;;;;

Function die_enemy()

    For i=1 To max
        If aEnemy(i)\status = die  Then
            If MilliSecs() >= timer + 700 Then
                aEnemy(i)\status = reborn
                FreeEntity aEnemy(i)\model
                FreeEntity aEnemy(i)\pivot_enemy
                
                timer = 0
                num_enemy = num_enemy - 1
            EndIf
        EndIf
    Next
End Function
;;;;;;;;;;;;;;
Function sleep()
For i=1 To max
    aEnemy(i)\speed_enemy# = 0
Next

    If god = True
       AmbientLight 50,50,50
       MoveEntity  player , 0 , 0.3 , 0
    End If

    If EntityY(player) > 65
        god = False
        GAME_OVER = True
    EndIf
    
End Function
;;;;;;;;;;;;;;;;;;;;;

Function RebornEnemy()

    For i=1 To max
    
    If aEnemy(i)\status = reborn Then  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;REBORN!!!!!!!!!!!!!!
        num_enemy = num_enemy + 1
        poz = Rand(1,4)
        Select poz
            Case 1
                x = 40 +  Rand(-25, 25)
                z = 500 + Rand (-100 , 200 )
            Case 2
                x = -20 + Rand(-20,20)  
                z = -400 + Rand (-200 , 50 )
            Case 3
                x = 600 + Rand(-100, 100)
                z = Rnd(0,10)
            Case 4
                x = -600 + Rand(-100, 100)
                z = -23 + Rand(-20,20) 
        End Select

            bb# = Rnd(0 , 0.03) 
            gg# =  Rnd (0 , 0.2)
        aEnemy(i)\pivot_enemy = CreateSphere()
        ScaleEntity aEnemy(i)\pivot_enemy , 4,4,4
        
        EntityAlpha aEnemy(i)\pivot_enemy , alpha_sp
        aEnemy(i)\model= CopyEntity (e_go  , aEnemy(i)\pivot_enemy )
    
        PositionEntity aEnemy(i)\pivot_enemy , x ,  player_Y , z
        PositionEntity aEnemy(i)\model       , 0 , -3 , 0
        
        EntityType aEnemy(i)\pivot_enemy , typeEnemy
        EntityRadius aEnemy(i)\pivot_enemy, 8
        
        AnimateMD2 aEnemy(i)\model,1,0.06 + bb#
        
        EntityPickMode aEnemy(i)\pivot_enemy, 2 
        aEnemy(i)\speed_enemy# = 0.3 + gg# 
        aEnemy(i)\status = alive 
    EndIf   
Next

End Function
;;;;;;;;;;;;;;;;;;;;

Function UpdateGun()

    For s.spark=Each spark
        EntityAlpha s\entity,s\alpha
        s\alpha=s\alpha-0.05
        If s\alpha <= 0 Then FreeEntity s\entity : Delete s
    Next

End Function
;;;;;;;;;;

Function StepsSound()
  If steptick < MilliSecs()
     If iSoundWalk = 1
        iSoundWalk = 0
     Else 
        iSoundWalk = 1
     EndIf
     PlaySound( aWalk( iSoundWalk ) ) 
     steptick = MilliSecs() + 500
  EndIf
End Function
;;;;;;;;;;;
Function die_sound(etc)
    x=CreateCube()
        PositionEntity x, EntityX(etc),EntityY(etc),EntityZ(etc)
        EntityAlpha x,0
            rd = Rand(1,2)
        Select rd
            Case 1
                EmitSound die1,x
            Case 2
                EmitSound die2,x
        End Select
    FreeEntity x
        
End Function
;;;;;;;
Function new_game_sound()
    fg = Rand(0,1)
        Select fg
           Case 0
             PlaySound new_game_sound1
           Case 1
             PlaySound new_game_sound2
    End Select
End Function 
;;;;;;;;;;;;;;;;

Function FreshBlood() 

    ; New blood drops
    For i=1 To blood_intensity
        b.blood=New blood
        b\y_speed=0
        b\alpha=2
        b\entity=CopyEntity(blood)
        PositionEntity  b\entity,PickedX(),PickedY(),PickedZ()
        ResetEntity b\entity
        scale_randx#=Rnd(0.2,2.9)
        scale_randy#=scale_randx#+Rnd(-0.1,0.1)
        ScaleSprite b\entity,scale_randx#,scale_randy#
        RotateEntity b\entity,Rnd(360),Rnd(360),Rnd(360)
    Next
    
End Function
;;;;;;;;;;;;;;;;;;

Function SpurtBlood()

    ; For each drop of blood...
    For b.blood=Each blood
        
    ; If blood drop collides with camera then stick blood drop on camera
    If EntityCollided(b\entity,type_camera)<>0 Then EntityParent b\entity,player : EntityType b\entity,0 : b\alpha=1 : b\stuck=True
    
        ; Update blood drops
        If b\alpha>0
            EntityAlpha b\entity,b\alpha
            If b\stuck<>True Then MoveEntity b\entity,0,0,1
            If b\stuck<>True Then TranslateEntity b\entity,0,b\y_speed,0
            If b\stuck<>True Then b\alpha=b\alpha-0.01
            If b\stuck=True Then b\alpha=b\alpha-mouse_shake
            b\y_speed=b\y_speed-0.025
        Else
            FreeEntity b\entity
            Delete b
        EndIf
        
    Next

End Function
;;;;;;;;;;;;;;;;;;;;;;;;;;;
