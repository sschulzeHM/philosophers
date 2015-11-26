package vss.distributed.philosophers;

import java.io.Serializable;
import java.rmi.Remote;

public interface IRegisterObject extends Serializable{
	String getName();
	Remote getObject();
}
