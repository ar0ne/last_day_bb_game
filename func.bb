Function wakeup()

	If bWakeup = True Then

		For i=1 To 230
			Delay 30
			AmbientLight i,i,i
			Flip
			RenderWorld
		Next
	bWakeup = False
	End If
End Function
; ---------



Function skybox(size,t1$,t2$,t3$,t4$,t5$,t6$)
sky=CreateMesh() ;//create mesh

sfr=CreateSurface(sky) ;//front
v1=AddVertex(sfr,-1,-1,1,1,1)
v2=AddVertex(sfr,1,-1,1,0,1)
v3=AddVertex(sfr,-1,1,1,1,0)
v4=AddVertex(sfr,1,1,1,0,0)
AddTriangle(sfr,v3,v2,v1)
AddTriangle(sfr,v3,v4,v2)
br=LoadBrush(t1$,49)
PaintSurface sfr,br
FreeBrush br

sfr=CreateSurface(sky) ;//left
v1=AddVertex(sfr,-1,-1,-1,1,1)
v2=AddVertex(sfr,-1,-1,1,0,1)
v3=AddVertex(sfr,-1,1,-1,1,0)
v4=AddVertex(sfr,-1,1,1,0,0)
AddTriangle(sfr,v3,v2,v1)
AddTriangle(sfr,v3,v4,v2)
br=LoadBrush(t2$,49)
PaintSurface sfr,br
FreeBrush br

sfr=CreateSurface(sky) ;//right
v1=AddVertex(sfr,1,-1,-1,0,1)
v2=AddVertex(sfr,1,-1,1,1,1)
v3=AddVertex(sfr,1,1,-1,0,0)
v4=AddVertex(sfr,1,1,1,1,0)
AddTriangle(sfr,v1,v2,v3)
AddTriangle(sfr,v2,v4,v3)
br=LoadBrush(t3$,49)
PaintSurface sfr,br
FreeBrush br

sfr=CreateSurface(sky) ;//back
v1=AddVertex(sfr,-1,-1,-1,0,1)
v2=AddVertex(sfr,1,-1,-1,1,1)
v3=AddVertex(sfr,-1,1,-1,0,0)
v4=AddVertex(sfr,1,1,-1,1,0)
AddTriangle(sfr,v1,v2,v3)
AddTriangle(sfr,v3,v2,v4)
br=LoadBrush(t4$,49)
PaintSurface sfr,br
FreeBrush br

sfr=CreateSurface(sky) ;//bottom
v1=AddVertex(sfr,-1,-1,-1,0,1)
v2=AddVertex(sfr,1,-1,-1,0,0)
v3=AddVertex(sfr,-1,-1,1,1,1)
v4=AddVertex(sfr,1,-1,1,1,0)
AddTriangle(sfr,v3,v2,v1)
AddTriangle(sfr,v3,v4,v2)
br=LoadBrush(t5$,49)
PaintSurface sfr,br
FreeBrush br

sfr=CreateSurface(sky) ;//top
v1=AddVertex(sfr,-1,1,-1,0,0)
v2=AddVertex(sfr,1,1,-1,0,1)
v3=AddVertex(sfr,-1,1,1,1,0)
v4=AddVertex(sfr,1,1,1,1,1)
AddTriangle(sfr,v1,v2,v3)
AddTriangle(sfr,v3,v2,v4)
br=LoadBrush(t6$,49)
PaintSurface sfr,br
FreeBrush br

EntityFX sky,1 ;//full-bright

ScaleEntity sky,size,size,size ;//size of skybox
End Function
;-------------------

