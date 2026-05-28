/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tankgame.server;
import java.util.UUID;
/**
 *
 * @author layne
 */
public class LobbyPlayer  {
    private String name;
    private UUID ID;
    
   public LobbyPlayer (String name, UUID ID){
       
       this.name = name;
       this.ID = ID;
   }
    
 
   
}
