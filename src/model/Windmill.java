package model;

import shader.Shader;
import loader.Loader;
import util.Matrix4f;

public class Windmill extends Model
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
        setInternalTransform(3, Matrix4f.translate(5, 9, 0));
        setInternalTransform(4, Matrix4f.translate(5, 9, 0).multiply(Matrix4f.rotate(90, 0, 0)));
        setInternalTransform(5, Matrix4f.translate(5, 9, 0).multiply(Matrix4f.rotate(180, 0, 0)));
        setInternalTransform(6, Matrix4f.translate(5, 9, 0).multiply(Matrix4f.rotate(270, 0, 0)));
    }

    @Override
    public void update()
    {
        internalTransform.set(3, internalTransform.get(3).multiply(Matrix4f.rotate(-0.5f, 0, 0)));
        internalTransform.set(4, internalTransform.get(4).multiply(Matrix4f.rotate(-0.5f, 0, 0)));
        internalTransform.set(5, internalTransform.get(5).multiply(Matrix4f.rotate(-0.5f, 0, 0)));
        internalTransform.set(6, internalTransform.get(6).multiply(Matrix4f.rotate(-0.5f, 0, 0)));
    }

}
