package model;

import loader.RawData;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shader.ModelShader;
import shader.Shader;

public class MultiPartModel extends Movable
{

    protected List<ColorModel> models = new ArrayList<>();

    public MultiPartModel(ModelShader shader, RawData... datas)
    {
        for (RawData data : datas)
        {
            models.add(new ColorModel(data));
        }
    }

    public void render(ModelShader shader)
    {
        for (int i = 0; i < models.size(); i++)
        {
            //TODO: needs to be moves to the position of the Multipartobject as well.
            // now it only has the internal positions.
            ColorModel m = models.get(i);
            m.render(shader);
        }
    }

    public void deactivate()
    {
        GL30.glBindVertexArray(0);

        GL20.glDisableVertexAttribArray(Shader.POS_ATTRIB);
        GL20.glDisableVertexAttribArray(Shader.TEX_ATTRIB);
        GL20.glDisableVertexAttribArray(Shader.NORMAL_ATTRIB);
    }

    public void destroy()
    {
        for (ColorModel m : models)
        {
            m.destroy();
        }
    }

    public void update()
    {
    }
}
