package state;

public class MenuState extends State
{

    @Override
    public void init()
    {

    }

    @Override
    public void update(float deltaTime)
    {
        System.out.println("hej menu");
    }

    @Override
    public void render(long window)
    {

    }

    @Override
    public void checkInput(long window)
    {

    }

    @Override
    public String name()
    {
        return "menu";
    }

}
