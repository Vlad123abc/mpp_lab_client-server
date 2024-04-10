package org.example.rpcProtocol;

import java.io.Serializable;

public class Request implements Serializable
{
    private RequestType type;
    private Object data;

    public Request() {}

    public RequestType getType()
    {
        return type;
    }

    public Object getData()
    {
        return data;
    }

    public void setType(RequestType type)
    {
        this.type = type;
    }

    public void setData(Object data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return "Request{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }

    public static class Builder
    {
        private Request request = new Request();

        public Builder type(RequestType type)
        {
            request.setType(type);
            return this;
        }

        public Builder data(Object data)
        {
            request.setData(data);
            return this;
        }

        public Request build()
        {
            return request;
        }
    }
}
