package KalendarUspomena.DTO.Exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorMessage {
  List<String> message;
  public ErrorMessage(){
    message = new ArrayList<String>();
  }
  public ErrorMessage(String s)
  {
    message=new ArrayList<String>();
    message.add(s);
  }
  public void add(String s)
  {message.add(s);}
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append("{\"message\": [\"").append(message.get(0));
    for (int i = 1; i < message.size(); i++)
      s.append("\",\"").append(message.get(i));
    s.append("\"]}");
    return s.toString();
  }
  @Schema(hidden = true)
  public boolean isEmpty()
  {
    return message.size()==0;
  }
}
