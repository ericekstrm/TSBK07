package main;

import util.Loader;
import java.nio.*;
import java.util.ArrayList;
import java.util.List;
import model.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;
import shader.Shader;
import util.Matrix4f;
import util.Vector3f;

public class main
{

    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    long window;

    Model model1;
    Model windmill;
    Model floor;
    Model tree;
    Skybox skybox;
    List<Light> pointLights = new ArrayList<>();
    
    int tex;

    Shader shader;
    Shader lightShader;
    Shader skyboxShader;

    Camera camera = new Camera(new Vector3f(2, 1, 2), new Vector3f(3, 3, 0));

    void initOpenGL()
    {
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        //create a window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Mitt fönster", NULL, NULL);
        if (window == 0)
        {
            System.out.println("Failed to create window.");
            glfwTerminate();
            return;
        }

        glfwMakeContextCurrent(window);

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);
        glfwShowWindow(window);

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        GL.createCapabilities();
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
    }

    public void destroyOpenGL()
    {
        // Disable the VBO index from the VAO attributes list
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        model1.destroy();
        windmill.destroy();
        tree.destroy();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public void initModel()
    {
        shader = new Shader("test.vert", "test.frag");
        lightShader = new Shader("light.vert", "light.frag");
        skyboxShader = new Shader("skybox.vert", "skybox.frag");

        model1 = new Model(shader, Loader.loadRawData("bunnyplus.obj", "tex2.jpg"));
        model1.setPosition(1, 0, 1);

        windmill = new Model(shader, Loader.loadRawData("windmill/walls.obj", "tex.jpg"),
                             Loader.loadRawData("windmill/balcony.obj", "tex.jpg"),
                             Loader.loadRawData("windmill/roof.obj", "tex.jpg"),
                             Loader.loadRawData("windmill/blade.obj", "tex.jpg"),
                             Loader.loadRawData("windmill/blade.obj", "tex.jpg"),
                             Loader.loadRawData("windmill/blade.obj", "tex.jpg"),
                             Loader.loadRawData("windmill/blade.obj", "tex.jpg"));
        windmill.setInternalTransform(3, Matrix4f.translate(5, 9, 0));
        windmill.setInternalTransform(4, Matrix4f.translate(5, 9, 0).multiply(Matrix4f.rotate(90, 0, 0)));
        windmill.setInternalTransform(5, Matrix4f.translate(5, 9, 0).multiply(Matrix4f.rotate(180, 0, 0)));
        windmill.setInternalTransform(6, Matrix4f.translate(5, 9, 0).multiply(Matrix4f.rotate(270, 0, 0)));
        windmill.setPosition(10, 0, -10);
        windmill.setRotation(0, 180, 0);

        tree = new Model(shader, Loader.loadRawData("tree.obj", "green.jpg"));
        tree.setPosition(-2, 0, -2);
        tree.setScale(0.1f, 0.1f, 0.1f);

        floor = new Model(shader, Loader.loadRawData("flat.obj", "grass.jpg"));
        floor.setPosition(0, -0.1f, 0);
        floor.setScale(20, 1, 20);

        pointLights.add(new Light(new Vector3f(5.0f, 5.0f, 0.0f),
                                  new Vector3f(1.0f, 0.0f, 0.0f)));
        pointLights.add(new Light(new Vector3f(0.0f, 5.0f, 5.0f),
                                  new Vector3f(0.0f, 1.0f, 0.0f)));

        skybox = new Skybox(skyboxShader, Loader.loadRawData("skybox.obj", "SkyBox512.tga"));
    }

    long time = 0;

    public void update()
    {
        windmill.update();
        time = System.currentTimeMillis() % 36000;
        model1.setRotation(0, time / 10, 0);
        
        pointLights.get(0).setPosition(Matrix4f.rotate(0, 2, 0).multiply(pointLights.get(0).getPosition()));
    }

    //light sources (TEMP)
    Vector3f[] lightSourcesColorsArr =
    {
        new Vector3f(0.0f, 0.0f, 1.0f), // Blue light
        new Vector3f(1.0f, 1.0f, 1.0f)  // White light 
    };

    Vector3f[] lightSourcesDirectionsPositions =
    {
        new Vector3f(-1.0f, 0.0f, 0.0f), // Blue light along X
        new Vector3f(0.0f, 0.0f, -1.0f)  // White light along Z
    };

    float[] specularExponent =
    {
        100.0f, 200.0f, 60.0f, 50.0f, 300.0f, 150.0f
    };

    void loop()
    {
        while (!glfwWindowShouldClose(window))
        {
            update();

            //prepare
            glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            //draw skybox
            skyboxShader.start();
            
            skybox.prepareForRender(camera, skyboxShader);
            skybox.render(shader);
            
            skyboxShader.stop();
            
            //draw lights
            lightShader.start();

            //world-to-view matrix
            FloatBuffer worldToView = BufferUtils.createFloatBuffer(16);
            camera.getWorldtoViewMatrix().toBuffer(worldToView);
            glUniformMatrix4fv(glGetUniformLocation(lightShader.getProgramID(), "worldToView"), false, worldToView);
            
            for (Light light : pointLights)
            {
                light.render(lightShader);
            }
            lightShader.stop();

            shader.start();
            loadLights();
            
            FloatBuffer viewPos = BufferUtils.createFloatBuffer(3);
            camera.getPosition().toBuffer(viewPos);
            glUniformMatrix3fv(glGetUniformLocation(shader.getProgramID(), "viewPos"), false, viewPos);

            //world-to-view matrix
            glUniformMatrix4fv(glGetUniformLocation(shader.getProgramID(), "worldToView"), false, worldToView);

            //render
            model1.render(shader);
            windmill.render(shader);
            tree.render(shader);
            floor.render(shader);

            shader.stop();

            glfwSwapBuffers(window);

            glfwPollEvents();
            checkInput();
        }

        glfwTerminate();
    }

    public void checkInput()
    {
        camera.checkInput(window);

        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        {
            glfwSetWindowShouldClose(window, true);
        }
    }

    public void loadLights()
    {
        //Pointlights position
        FloatBuffer pointLightPosArr = BufferUtils.createFloatBuffer(3 * pointLights.size());
        for (Light light : pointLights)
        {
            Vector3f pos = light.getPosition();
            pointLightPosArr.put(pos.x).put(pos.y).put(pos.z);
        }
        pointLightPosArr.flip();
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "pointLightPosArr"), pointLightPosArr);

        //Pointlights color
        FloatBuffer pointLightColorArr = BufferUtils.createFloatBuffer(3 * pointLights.size());
        for (Light light : pointLights)
        {
            Vector3f color = light.getColor();
            pointLightColorArr.put(color.x).put(color.y).put(color.z);
        }
        pointLightColorArr.flip();
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "pointLightColorArr"), pointLightColorArr);

        //SKA GÖRAS BÄTTRE!!
        //Directional lights directions
        FloatBuffer dirLightDirArr = BufferUtils.createFloatBuffer(6);
        for (int i = 0; i < 2; i++)
        {
            dirLightDirArr.put(lightSourcesDirectionsPositions[i].x).put(lightSourcesDirectionsPositions[i].y).put(lightSourcesDirectionsPositions[i].z);
        }
        dirLightDirArr.flip();
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "dirLightDirArr"), dirLightDirArr);

        //Directional lights color
        FloatBuffer dirLightColorArr = BufferUtils.createFloatBuffer(6);
        for (int i = 0; i < 2; i++)
        {
            dirLightColorArr.put(lightSourcesColorsArr[i].x).put(lightSourcesColorsArr[i].y).put(lightSourcesColorsArr[i].z);
        }
        dirLightColorArr.flip();
        glUniform3fv(glGetUniformLocation(shader.getProgramID(), "dirLightColorArr"), dirLightColorArr);

        //specular
        //should be uploaded for each model (or part of model)
        glUniform1f(glGetUniformLocation(shader.getProgramID(), "specularExponent"), specularExponent[0]);
    }

    public static void main(String[] args)
    {
    	System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
    	
        main m = new main();
        m.initOpenGL();
        m.initModel();
        m.loop();
        m.destroyOpenGL();
    }
}
