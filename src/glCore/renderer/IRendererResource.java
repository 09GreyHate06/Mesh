package glCore.renderer;

public interface IRendererResource  {
    int getRendererID();
    void bind();
    void unbind();
    boolean isValid();
}
