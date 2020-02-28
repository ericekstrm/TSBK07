package model;

import shader.Shader;
import loader.Loader;
import util.Matrix4f;
import util.Vector3f;

public class Windmill extends MultiPartModel
{

    public Windmill(Shader shader)
    {
        super(shader, Loader.loadRawData("windmill/walls.obj", "tex.jpg"),
              Loader.loadRawData("windmill/balcony.obj", "tex.jpg"),
              Loader.loadRawData("windmill/roof.obj", "tex.jpg"),
              Loader.loadRawData("windmill/blade.obj", "tex.jpg"),
              Loader.loadRawData("windmill/blade.obj", "tex.jpg"),
              Loader.loadRawData("windmill/blade.obj", "tex.jpg"),
              Loader.loadRawData("windmill/blade.obj", "tex.jpg"));
        models.get(3).setPosition(5, 9, 0);
        models.get(4).setPosition(5, 9, 0);
        models.get(5).setPosition(5, 9, 0);
        models.get(6).setPosition(5, 9, 0);
        models.get(4).setRotation(90, 0, 0);
        models.get(5).setRotation(180, 0, 0);
        models.get(6).setRotation(270, 0, 0);
    }

    public void update(long time)
    {
        models.get(3).rotate(-0.5f, 0, 0);
        models.get(4).rotate(-0.5f, 0, 0);
        models.get(5).rotate(-0.5f, 0, 0);
        models.get(6).rotate(-0.5f, 0, 0);
    }
}
