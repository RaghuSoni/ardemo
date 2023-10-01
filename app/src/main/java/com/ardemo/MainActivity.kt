package com.ardemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.utils.setFullScreen


class MainActivity : AppCompatActivity() {
    lateinit var sceneView: ArSceneView
    lateinit var placeButton: ExtendedFloatingActionButton
    lateinit var addNew: ExtendedFloatingActionButton
    var modelNode: ArModelNode? =null

    data class Model(
        val fileLocation: String,
        val scaleUnits: Float? = null,
        val placementMode: PlacementMode = PlacementMode.BEST_AVAILABLE,
        val applyPoseRotation: Boolean = true
    )

    val models = listOf(
        Model(
            fileLocation = "models/sofa_chair.glb",
            scaleUnits = 1.0f,
            placementMode = PlacementMode.INSTANT,
            applyPoseRotation = false
        ),
        Model(
            fileLocation = "models/dining_table.glb",
            placementMode = PlacementMode.BEST_AVAILABLE,
            scaleUnits = 1.5f
        ),
        Model(
            fileLocation = "models/cabinet.glb",
            placementMode = PlacementMode.BEST_AVAILABLE,
            scaleUnits = 1.0f
        )
    )

    var modelIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setFullScreen(
            findViewById(R.id.root_view),
            fullScreen = true,
            hideSystemBars = false,
            fitsSystemWindows = false
        )

        sceneView = findViewById(R.id.scene_view)
        placeButton = findViewById(R.id.place)
        addNew = findViewById(R.id.add_new)

        placeButton.setOnClickListener {
            placeModelNode()
        }
        addNew.setOnClickListener{
            newModelNode()
        }


    newModelNode()

    }

    private fun placeModelNode(){
        modelNode?.anchor()
        placeButton.isVisible = false
        sceneView.planeRenderer.isVisible = false
    }

    fun newModelNode() {

        modelNode?.takeIf { !it.isAnchored }?.let {
            sceneView.removeChild(it)
            it.destroy()
        }
        val model = models[modelIndex]
        modelIndex = (modelIndex + 1) % models.size
        modelNode = ArModelNode(sceneView.engine, model.placementMode).apply {
            isSmoothPoseEnable = true
            applyPoseRotation = model.applyPoseRotation
            loadModelGlbAsync(
                glbFileLocation = model.fileLocation,
                autoAnimate = true,
                scaleToUnits = model.scaleUnits,
                centerOrigin = Position(y = -1.0f)
            ) {
                sceneView.planeRenderer.isVisible = true

            }
            onAnchorChanged = { anchor ->
                placeButton.isGone = anchor != null
            }
            onHitResult = { node, _ ->
                placeButton.isGone = !node.isTracking
            }
        }
        sceneView.addChild(modelNode!!)
        sceneView.selectedNode = modelNode
    }

}