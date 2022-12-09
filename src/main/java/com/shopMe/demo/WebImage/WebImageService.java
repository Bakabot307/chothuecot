package com.shopMe.demo.WebImage;

import com.shopMe.demo.exceptions.WebImageException;
import java.util.Comparator;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class WebImageService {

  final long banner = 5;
  final long about = 4;
  final long logo = 1;
  @Autowired
  private WebImageRepository repo;

  public WebImage addImage(WebImage image) {
    image.setActive(false);
    return repo.save(image);
  }

  public WebImage update(WebImage image) {
    return repo.save(image);
  }

  public WebImage getById(Integer id) throws WebImageException {
    return repo.findById(id).orElseThrow(() -> new WebImageException("Không tìm thấy hình ảnh"));
  }

  public List<WebImage> getImage(String category) throws WebImageException {

    List<WebImage> list = repo.findByCategory(category);
    if (list == null || list.isEmpty()) {
      throw new WebImageException("Không tìm thấy hình ảnh");
    }

    switch (category) {
      case "banner" -> list = list.stream().limit(banner).toList();
      case "about" -> list = list.stream().limit(about).toList();
      case "logo" -> list = list.stream().limit(logo).toList();
      default -> {
      }
    }

    return list;
  }

  public void delete(WebImage wI) {
    repo.delete(wI);
  }

  public List<WebImage> getALl() {
    return repo.findAll();
  }


  public void setActive(Integer id){
    WebImage wI = repo.findById(id).get();
    wI.setActive(true);
    repo.save(wI);
  }


}
