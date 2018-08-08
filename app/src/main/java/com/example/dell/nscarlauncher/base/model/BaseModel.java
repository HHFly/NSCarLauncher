package com.example.dell.nscarlauncher.base.model;





import com.example.dell.nscarlauncher.common.util.JsonUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * model的基类
 */

public class BaseModel implements Serializable {

    public String toJson() {
        return JsonUtils.toJson(this);
    }

    /**
     * 克隆
     *
     * @param <T>
     * @return
     */
    public <T extends BaseModel> T cloneSelf() {
        return (T) cloneTo(this);
    }

    /**
     * 克隆
     *
     * @param src
     * @param <T>
     * @return
     * @throws RuntimeException
     */
    @SuppressWarnings("unchecked")
    private <T> T cloneTo(T src) throws RuntimeException {
        ByteArrayOutputStream memoryBuffer = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        T dist = null;
        try {
            out = new ObjectOutputStream(memoryBuffer);
            out.writeObject(src);
            out.flush();
            in = new ObjectInputStream(new ByteArrayInputStream(memoryBuffer.toByteArray()));
            dist = (T) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null)
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            if (in != null)
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
        return dist;
    }
}
