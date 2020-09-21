package cga.exercise.game

import cga.exercise.components.camera.BirdsEyeCamera
import cga.exercise.components.camera.EyeCamera
import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.Math
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow)  {
    private val tronShader: ShaderProgram
    private val standardShader: ShaderProgram
    private val toonShader: ShaderProgram
    private var activeShader: ShaderProgram


    var bodenr: Renderable
    var wandv: Renderable
    var wandh: Renderable
    var wandr: Renderable
    var wandl: Renderable
    var camera :TronCamera
    var eyecamera :EyeCamera
    var birdseyecamera :BirdsEyeCamera
    var material :Material
    var wall_material :Material
    var diff :Texture2D
    var emit :Texture2D
    var spec : Texture2D
    var wall_emit: Texture2D
    var person: Renderable
    var cent: Renderable
    var pointlight :PointLight
    var spotligt :SpotLight

    var coin_position = Vector3f(0f,1.5f,0f)
    var coin_spawn_range = 15
    var coinPoints = 0


    //scene setup
    init {
        tronShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
        standardShader = ShaderProgram("assets/shaders/standard_vert.glsl", "assets/shaders/standard_frag.glsl")
        toonShader = ShaderProgram("assets/shaders/toon_vert.glsl", "assets/shaders/toon_frag.glsl")


        activeShader = standardShader

        //person = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj",Math.toRadians(-90f),Math.toRadians(45f),Math.toRadians(0.0f)) ?: throw IllegalArgumentException("Could not load the model")
        //person = ModelLoader.loadModel("assets/models/person.obj",Math.toRadians(0f),Math.toRadians(180f),Math.toRadians(0.0f)) ?: throw IllegalArgumentException("Could not load the model")
        person = ModelLoader.loadModel("assets/models/star wars republic guard.obj",Math.toRadians(0f),Math.toRadians(180f),Math.toRadians(0.0f)) ?: throw IllegalArgumentException("Could not load the model")

        cent = ModelLoader.loadModel("assets/models/50cent.obj",Math.toRadians(90.0f),Math.toRadians(180.0f),Math.toRadians(90.0f)) ?: throw IllegalArgumentException("Could not load the model")


        //person.scaleLocal(Vector3f(0.4f,0.4f,0.4f))
        person.scaleLocal(Vector3f(0.3f,0.3f,0.3f))

        cent.scaleLocal(Vector3f(0.8f,0.8f,0.8f))

        person.translateLocal(Vector3f(0f,0f,0f))
        cent.translateLocal(coin_position)
        coin_position = Vector3f((Math.random()*20).toFloat()-10,0f,(Math.random()*20).toFloat()-10)
        cent.translateLocal(coin_position)


        val res2: OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
        val objMesh2: OBJLoader.OBJMesh = res2.objects[0].meshes[0]

        val res3: OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/wall.obj")
        val objMesh3: OBJLoader.OBJMesh = res3.objects[0].meshes[0]

        diff = Texture2D.invoke("assets/textures/ground_diff.png",true)
        diff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        emit = Texture2D.invoke("assets/textures/ground_dirt.png",true)
        emit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        spec = Texture2D.invoke("assets/textures/ground_spec.png",true)
        spec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        wall_emit = Texture2D.invoke("assets/textures/wall.png",true)
        wall_emit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        material = Material(diff,emit,spec,60f, Vector2f(2f,2f))
        wall_material = Material(diff, wall_emit,spec, 30f, Vector2f(1f, 2f))




        val stride: Int = 8 * 4
        val attrPos = VertexAttribute(3, GL15.GL_FLOAT, stride, 0)
        val attrTC =  VertexAttribute(2, GL15.GL_FLOAT, stride, 3 * 4)
        val attrNorm =  VertexAttribute(3, GL15.GL_FLOAT, stride, 5 * 4)

        val vertexAttributes = arrayOf(attrPos, attrTC, attrNorm)
        //kugelMesh = Mesh(objMesh.vertexData, objMesh.indexData, vertexAttributes)
        val bodenmesh  = Mesh(objMesh2.vertexData, objMesh2.indexData, vertexAttributes,material)
        val wallmesh = Mesh(objMesh2.vertexData, objMesh2.indexData, vertexAttributes,wall_material)

        bodenr = Renderable(mutableListOf(bodenmesh))
        wandv = Renderable(mutableListOf(wallmesh))
        wandh = Renderable(mutableListOf(wallmesh))
        wandr = Renderable(mutableListOf(wallmesh))
        wandl = Renderable(mutableListOf(wallmesh))


        wandv.rotateAroundPoint(0.0f, Math.toRadians(90.0f), Math.toRadians(-90.0f), Vector3f(0.0f))
        wandh.rotateAroundPoint(0.0f, Math.toRadians(90.0f), Math.toRadians(90.0f), Vector3f(0.0f))
        wandr.rotateAroundPoint(0.0f, Math.toRadians(0.0f), Math.toRadians(90.0f), Vector3f(0.0f))
        wandl.rotateAroundPoint(0.0f, Math.toRadians(0.0f), Math.toRadians(-90.0f), Vector3f(0.0f))

        wandv.translateGlobal(Vector3f(-11.0f, 0.0f, 21.0f))
        wandh.translateGlobal(Vector3f(11.0f, 0.0f, -21.0f))
        wandr.translateGlobal(Vector3f(21.0f, 0.0f, -11.0f))
        wandl.translateGlobal(Vector3f(-21.0f, 0.0f, 11.0f))
        wandv.scaleLocal(Vector3f(0.8f,0.8f,2.8f))
        wandh.scaleLocal(Vector3f(0.8f,0.8f,2.8f))
        wandr.scaleLocal(Vector3f(0.8f,0.8f,2.8f))
        wandl.scaleLocal(Vector3f(0.8f,0.8f,2.8f))
        //kugelr = Renderable(mutableListOf(kugelMesh))


        //bodenr.rotateLocal(Math.toRadians(90.0f),0f,0f)
        //bodenr.scaleLocal(Vector3f(0.03f,0.03f,0.03f))
        // kugelr.scaleLocal(Vector3f(0.5f,0.5f,0.5f))


        camera = TronCamera()
        camera.rotateLocal(Math.toRadians(-35.0f),0f,0f)
        camera.translateLocal(Vector3f(0.0f,1.0f,15f))
        camera.parent = person

        eyecamera = EyeCamera()
        eyecamera.rotateLocal(Math.toRadians(-20.0f),Math.toRadians(0.0f),Math.toRadians(0.0f))
        eyecamera.translateLocal(Vector3f(0.0f,0.0f,4.0f))
        eyecamera.parent = person

        birdseyecamera = BirdsEyeCamera()
        birdseyecamera.rotateLocal(Math.toRadians(-90.0f),Math.toRadians(0.0f),Math.toRadians(0.0f))
        birdseyecamera.translateLocal(Vector3f(0.0f,0.0f,15.0f))


        pointlight = PointLight(Vector3f(0f,2f,0f),Vector3f(1f,1f,1f))
        pointlight.parent = person


        spotligt = SpotLight(Vector3f(0f,2f,0f),Vector3f(1f,1f,1f),Math.cos(Math.toRadians(0f)),Math.cos(Math.toRadians(0f)))
        spotligt.parent = person


        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()


        glEnable(GL_CULL_FACE)
        glFrontFace(GL_CCW)
        glCullFace(GL_BACK)



    }

    fun render(dt: Float, t: Float) {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        activeShader.use()
        camera.bind(activeShader)
        activeShader.setUniform("shadingcolor",Vector3f(1f,0f,0f))
        pointlight.bind(activeShader,"coin")
        spotligt.bind(activeShader," ", camera.getCalculateViewMatrix())
        activeShader.setUniform("shadingcolor",Vector3f(0f,1f,0f))
        bodenr.render(activeShader)
        wandv.render(activeShader)
        wandh.render(activeShader)
        wandr.render(activeShader)
        wandl.render(activeShader)
        person.render(activeShader)
        cent.render(activeShader)
    }

    fun update(dt: Float, t: Float) {
        if(window.getKeyState(GLFW_KEY_W) && person.getPosition().x < 20.0f && person.getPosition().y < 20.0f && person.getPosition().z < 20.0f && person.getPosition().x > -20.0f && person.getPosition().y > -20.0f && person.getPosition().z > -20.0f){
            person.translateLocal(Vector3f(0.0f,0.0f,-10.0f*dt))
        }
        if(window.getKeyState(GLFW_KEY_S) && person.getPosition().x < 22.0f && person.getPosition().y < 22.0f && person.getPosition().z < 22.0f && person.getPosition().x > -22.0f && person.getPosition().y > -22.0f && person.getPosition().z > -22.0f){
            person.translateLocal(Vector3f(0f,0f,10.0f*dt))

        }
        if(window.getKeyState(GLFW_KEY_A) && person.getPosition().x < 20.0f && person.getPosition().y < 20.0f && person.getPosition().z < 20.0f && person.getPosition().x > -20.0f && person.getPosition().y > -20.0f && person.getPosition().z > -20.0f){
            person.translateLocal(Vector3f(0.0f,0.0f,0.0f*dt))
            person.rotateLocal(0.0f,2f*dt,0.0f)
        }
        if(window.getKeyState(GLFW_KEY_D) && person.getPosition().x < 20.0f && person.getPosition().y < 20.0f && person.getPosition().z < 20.0f && person.getPosition().x > -20.0f && person.getPosition().y > -20.0f && person.getPosition().z > -20.0f){

            person.translateLocal(Vector3f(0.0f,0.0f,0.0f*dt))
            person.rotateLocal(0.0f,-2f*dt,0.0f)

        }

        if(window.getKeyState(GLFW_KEY_M)){
            println(cent.getPosition()[0])
            println(person.getPosition()[0])

        }


        var cent_pos = cent.getPosition()
        var person_pos = person.getPosition()
        if((person_pos[0] >= cent_pos[0]-0.5f && person_pos[0] <= cent_pos[0]+0.5f) && (person_pos[2] >= cent_pos[2]-0.5f && person_pos[2] <= cent_pos[2]+0.5f)) {
            coin_position.negate()
            cent.translateLocal(coin_position)
            coinPoints = coinPoints + 50
            println("Points: " + coinPoints)
            coin_position = Vector3f((Math.random()*coin_spawn_range*2).toFloat()-coin_spawn_range,0f,(Math.random()*coin_spawn_range*2).toFloat()-coin_spawn_range)
            cent.translateLocal(coin_position)
        }


    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
        if(window.getKeyState(GLFW_KEY_1)){
            activeShader = standardShader
        }
        if(window.getKeyState(GLFW_KEY_2)){
            activeShader = tronShader
        }
        if(window.getKeyState(GLFW_KEY_3)){
            activeShader = toonShader
        }
    }



    fun onMouseMove(xpos: Double, ypos: Double) {

        var olpx =0.0
        var oldpy =0.0

        val distanceX = window.mousePos.xpos - olpx
        val distanceY = window.mousePos.ypos - oldpy

        if(distanceX > 0){
            //camera.rotateLocal(0f,Math.toRadians(distanceX.toFloat() *0.02f),0f)
        }
        if(distanceX < 0){
          // camera.rotateLocal(0f,Math.toRadians(distanceX.toFloat() * 0.02f),0f)
        }
        if(distanceY > 0){
           // camera.translateLocal(Vector3f(0f,0.02f,0f))
        }
        if(distanceY < 0){
           // camera.translateLocal(Vector3f(0f,-0.02f,0f))
        }

      olpx = window.mousePos.xpos
        oldpy = window.mousePos.ypos





    }


    fun cleanup() {}

}
