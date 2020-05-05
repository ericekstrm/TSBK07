package state;

public abstract class State
{
    int x;
    
    protected String changeState = "";

    /**
     * Initializes the state
     */
    public abstract void init();

    /**
     * Updates the state.
     * 
     * @param deltaTime - gives the time since the last update, in seconds.
     */
    public abstract void update(float deltaTime);

    /**
     * Renders the state to the screen.Called as fast as possible.
     *
     * @param window - the window that the scene will be rendered to.
     */
    public abstract void render(long window);

    /**
     * Checks  input from keyboard and mouse. Does not support callback events yet.
     * 
     * @param window 
     */
    public abstract void checkInput(long window);
    
    /**
     * Is called when the state is activated.
     * 
     * @param window
     * @param t
     */
    public abstract void activateState(long window, TransitionInformation t);
    
    /**
     * Is called when the state is deactivated.
     * 
     * @param window
     * @return 
     */
    public abstract TransitionInformation deactivateState(long window);
    
    /**
     * 
     * @return - the name of the state
     */
    public abstract String name();
    
    /**
     * Checks if the state should be changed.
     * 
     * @return Returns the changeState flag and resets the flag to an empty string.
     */
    public String updateState()
    {
        String tmp = changeState;
        changeState = "";
        return tmp;
    }
}
