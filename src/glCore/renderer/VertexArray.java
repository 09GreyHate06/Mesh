package glCore.renderer;

import glCore.core.Ref;
import org.lwjgl.opengl.GL33;

import java.util.ArrayList;
import java.util.List;

public class VertexArray extends Ref implements IRendererResource{

    private final int _rendererID;
    private boolean _isValid;

    private final List<VertexBuffer> _vertexBuffers;
    private IndexBuffer _indexBuffer;

    public VertexArray(){
        _rendererID = GL33.glGenVertexArrays();
        _isValid = true;
        _vertexBuffers = new ArrayList<>();
        _indexBuffer = null;
    }

    public void addVertexBuffer(VertexBuffer vertexBuffer){
        assert vertexBuffer.getLayout().getElements().size() > 0 : "VertexBuffer has no layout";

        bind();
        vertexBuffer.bind();

        var layout = vertexBuffer.getLayout();
        for(final var element : layout.getElements()){
            GL33.glEnableVertexAttribArray(element.attribIndex);
            GL33.glVertexAttribPointer(element.attribIndex, element.count, GL33.GL_FLOAT,
                    element.normalized, layout.getStride(), element.offset);
        }

        _vertexBuffers.add(vertexBuffer);
    }

    public void addIndexBuffer(IndexBuffer indexBuffer){
        bind();
        indexBuffer.bind();
        _indexBuffer = indexBuffer;
    }

    public List<VertexBuffer> getVertexBuffers(){
        return _vertexBuffers;
    }

    public IndexBuffer getIndexBuffer(){
        return _indexBuffer;
    }

    @Override
    public int getRendererID() {
        return _rendererID;
    }

    @Override
    public void bind() {
        assert _isValid : "Trying to bind destroyed Vertex Array";
        GL33.glBindVertexArray(_rendererID);
    }

    @Override
    public void unbind() {
        assert _isValid : "Trying to unbind destroyed Vertex Array";
        GL33.glBindVertexArray(0);
        _indexBuffer.unbind();
    }

    @Override
    public boolean isValid() {
        return _isValid;
    }

    @Override
    protected void destroy(){
        for(var vb : _vertexBuffers)
            vb.release();

        _indexBuffer.release();

        GL33.glDeleteVertexArrays(_rendererID);
        _isValid = false;
        System.out.println("Vertex Array destroyed");
    }
}
