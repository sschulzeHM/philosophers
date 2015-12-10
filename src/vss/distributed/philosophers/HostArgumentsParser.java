package vss.distributed.philosophers;

import java.rmi.registry.Registry;

/**
 * Created by stefanschulze on 29.11.15.
 */
public class HostArgumentsParser
{
    public static int getPortFromArgs(String[] args, int index)
    {
        int port = 0;
        if (args.length < (index - 1))
        {
            port = Registry.REGISTRY_PORT;
        }
        else
        {
            try
            {
                port = Integer.parseInt(args[index]);
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

    public static String getHostFromArgs(String[] args, int index)
    {
        String host = "localhost";
        if (args.length > (index - 1))
        {
            host = args[index];
        }
        return host;
    }
}
