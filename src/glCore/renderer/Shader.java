package glCore.renderer;

import glCore.core.Ref;
import org.joml.*;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import static org.lwjgl.system.MemoryStack.stackPush;


public class Shader extends Ref implements IRendererResource {

    enum Type{
        None(-1),
        Vertex(0),
        Fragment(1);
        // Geometry

        public final int label;

        private Type(int label){
            this.label = label;
        }
    }

    private int _rendererID;
    private boolean _isValid;
    private final HashMap<String, Integer> _uniformLocationCache = new HashMap<>();
    private final HashMap<String, Integer> _uniformBlockLocationCache = new HashMap<>();

    public Shader(String vertSrc, String fragSrc){
        init(vertSrc, fragSrc);
    }

    public Shader(String filename){
        StringBuilder vSrc = new StringBuilder();
        StringBuilder fSrc = new StringBuilder();

        try{
            File srcFile = new File(filename);
            Scanner scanner = new Scanner(srcFile);

            Type type = Type.None;
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();

                if(line.contains("#type")){
                    if(line.contains("vertex"))
                        type = Type.Vertex;
                    else if(line.contains("fragment"))
                        type = Type.Fragment;
                    else
                        type = Type.None;
                }
                else
                {
                    assert type != Type.None : "Unknown shader type!";
                    switch (type){
                        case Vertex:   vSrc.append(line).append('\n'); break;
                        case Fragment: fSrc.append(line).append('\n'); break;
                    }
                }
            }

        }catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        init(vSrc.toString(), fSrc.toString());
    }

    public void setUniformBool(String name, boolean value){
        GL33.glUniform1i(getUniformLocation(name), value ? 1 : 0);
    }

    public void setUniformInt(String name, int value){
        GL33.glUniform1i(getUniformLocation(name), value);
    }

    public void setUniformFloat(String name, float value){
        GL33.glUniform1f(getUniformLocation(name), value);
    }

    public void setUniformVec2(String name, Vector2f value){
        try(MemoryStack stack = stackPush()){
            GL33.glUniform2fv(getUniformLocation(name), value.get(stack.mallocFloat(2)));
        }
    }

    public void setUniformVec3(String name, Vector3f value){
        try(MemoryStack stack = stackPush()){
            GL33.glUniform3fv(getUniformLocation(name), value.get(stack.mallocFloat(3)));
        }
    }

    public void setUniformVec4(String name, Vector4f value){
        try(MemoryStack stack = stackPush()){
            GL33.glUniform4fv(getUniformLocation(name), value.get(stack.mallocFloat(4)));
        }
    }

    public void setUniformMat3f(String name, Matrix3f value){
        try(MemoryStack stack = stackPush()){
            GL33.glUniformMatrix3fv(getUniformLocation(name), false, value.get(stack.mallocFloat(9)));
        }
    }

    public void setUniformMat4f(String name, Matrix4f value){
        try(MemoryStack stack = stackPush()){
            GL33.glUniformMatrix4fv(getUniformLocation(name), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniformBlockBinding(String name, int uniformBlockBinding){
        GL33.glUniformBlockBinding(_rendererID, getUniformBlockIndex(name), uniformBlockBinding);
    }

    @Override
    public int getRendererID() {
        return _rendererID;
    }

    @Override
    public void bind() {
        assert _isValid : "Trying to bind destroyed Shader";
        GL33.glUseProgram(_rendererID);
    }

    @Override
    public void unbind() {
        assert _isValid : "Trying to unbind destroyed Shader";
        GL33.glUseProgram(0);
    }

    @Override
    public boolean isValid() {
        return _isValid;
    }

    @Override
    protected void destroy() {
        GL33.glDeleteProgram(_rendererID);
        _isValid = false;
        System.out.println("Shader destroyed");
    }

    private void init(String vSrc, String fSrc){
        int vs = compileShader(GL33.GL_VERTEX_SHADER, vSrc);
        int fs = compileShader(GL33.GL_FRAGMENT_SHADER, fSrc);

        _rendererID = GL33.glCreateProgram();

        GL33.glAttachShader(_rendererID, vs);
        GL33.glAttachShader(_rendererID, fs);

        GL33.glLinkProgram(_rendererID);

        if(GL33.glGetProgrami(_rendererID, GL33.GL_LINK_STATUS) == GL33.GL_FALSE){
            String infoLog = GL33.glGetProgramInfoLog(_rendererID);
            assert false : "Failed to link program.\nInfo log: " + infoLog;
        }

        GL33.glValidateProgram(_rendererID);
        if(GL33.glGetProgrami(_rendererID, GL33.GL_VALIDATE_STATUS) == GL33.GL_FALSE){
            String infoLog = GL33.glGetProgramInfoLog(_rendererID);
            assert false : "Failed to validate program.\nInfo log: " + infoLog;
        }

        GL33.glDeleteShader(vs);
        GL33.glDeleteShader(fs);

        _isValid = true;
    }

    private int compileShader(int type, String src){
        int shader = GL33.glCreateShader(type);
        GL33.glShaderSource(shader, src);

        GL33.glCompileShader(shader);

        if(GL33.glGetShaderi(shader, GL33.GL_COMPILE_STATUS) == GL33.GL_FALSE ){
            String infoLog = GL33.glGetShaderInfoLog(shader);
            GL33.glDeleteShader(shader);

            assert false : "Failed to compile shader. \n Info Log: " + infoLog;
        }

        return shader;
    }

    private int getUniformLocation(String name){
        if(_uniformLocationCache.containsKey(name))
            return _uniformLocationCache.get(name);

        int location = GL33.glGetUniformLocation(_rendererID, name);
        if(location == -1)
            System.out.println("Shader::Warning Uniform '" + name + "' does not exists!");

        _uniformLocationCache.put(name, location);
        return location;
    }

    private int getUniformBlockIndex(String name){
        if(_uniformBlockLocationCache.containsKey(name))
            return _uniformBlockLocationCache.get(name);

        int location = GL33.glGetUniformBlockIndex(_rendererID, name);
        if(location == GL33.GL_INVALID_INDEX)
            System.out.println("Shader::Warning Uniform block '" + name + "' does not exists!");

        _uniformBlockLocationCache.put(name, location);
        return location;
    }
}
