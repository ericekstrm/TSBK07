package particle_system;

import camera.Camera;
import java.util.ArrayList;
import java.util.List;
import util.Matrix4f;
import util.Vector3f;

public class Smoke
{

    private int maxParticles = 100;
    private Vector3f windDirection = new Vector3f(1, 0, 0);
    private float windPower = 0.005f;
    private float liftPower = 0.02f;

    private Vector3f position = new Vector3f();

    private List<Particle> particles = new ArrayList<>();

    public Smoke(Matrix4f projectionMatrix, Vector3f position)
    {
        this.position = position;
        for (int i = 0; i < 10; i++)
        {
            particles.add(new Particle(projectionMatrix, position));
        }
    }

    public void update(float deltatime, Matrix4f projectionMatrix)
    {
        if (particles.size() < maxParticles && Math.random() > 0.95)
        {
            particles.add(new Particle(projectionMatrix, position));
        }
        List<Particle> particlesToRemove = new ArrayList<>();
        for (Particle p : particles)
        {
            Vector3f newWindDirection = windDirection.add(new Vector3f((float) Math.random(),
                                                                       (float) Math.random(),
                                                                       (float) Math.random()));
            Vector3f diff = newWindDirection.scale(windPower).add(new Vector3f(0, liftPower + (float) Math.random() * liftPower, 0));
            p.setPosition(p.getPosition().add(diff));

            if (p.getPosition().y > 10 && Math.random() > 0.99)
            {
                particlesToRemove.add(p);
            }
            Vector3f newScale = p.getScale().add(new Vector3f((float) Math.random() / 20,
                                                              (float) Math.random() / 20,
                                                              (float) Math.random() / 20));
            p.setScale(newScale.x, newScale.y, newScale.z);
        }
        for (Particle p : particlesToRemove)
        {
            particles.remove(p);
        }
    }

    public void render(Camera camera)
    {
        for (Particle p : particles)
        {
            p.render(camera);
        }
    }
}
