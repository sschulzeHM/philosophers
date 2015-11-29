package vss.distributed.philosophers;

import java.rmi.registry.Registry;

/**
 * Created by stefanschulze on 29.11.15.
 */
public class HostApplication extends Thread
{
    public static int getPortFromArgs(String[] args)
    {
        int port = 0;
        if (args.length < 1)
        {
            port = Registry.REGISTRY_PORT;
        }
        else
        {
            try
            {
                port = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e)
            {
                port = Registry.REGISTRY_PORT;
            }
        }
        if (port == 0)
        {
            port = Registry.REGISTRY_PORT;
        }
        return port;
    }

    public static String getHostFromArgs(String[] args)
    {
        String host = "localhost";
        if (args.length > 1)
        {
            host = args[1];
        }
        return host;
    }
}
