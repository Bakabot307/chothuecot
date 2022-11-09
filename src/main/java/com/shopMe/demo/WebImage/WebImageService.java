package com.shopMe.demo.WebImage;

import com.shopMe.demo.exceptions.WebImageException;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebImageService {

  final int banner = 5;
  final int about = 4;
  final int logo = 1;
  @Autowired
  private WebImageRepository repo;

  public WebImage addImage(WebImage image) {
    WebImage images = repo.findByProductThatHasOrder(image.getCategory()).stream().max(
        Comparator.comparingInt(WebImage::getOrderNumber)).orElse(null);

    switch (image.getCategory()) {
      case "banner":
        if (image.getOrderNumber() == null) {
          if (images == null) {
            image.setOrderNumber(1);
          } else if (images.getOrderNumber() >= banner) {
            image.setOrderNumber(null);
          } else {
            image.setOrderNumber(images.getOrderNumber() + 1);
          }
        }

        break;
      case "logo":
        if (image.getOrderNumber() == null) {
          if (images == null) {
            image.setOrderNumber(1);
          } else if (images.getOrderNumber() >= logo) {
            image.setOrderNumber(null);
          } else {
            image.setOrderNumber(images.getOrderNumber() + 1);
          }
        }
        break;
      case "about":
        if (image.getOrderNumber() == null) {
          if (images == null) {
            image.setOrderNumber(1);
          } else if (images.getOrderNumber() >= about) {
            image.setOrderNumber(null);
          } else {
            image.setOrderNumber(images.getOrderNumber() + 1);
          }
        }
        break;
    }

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

    return list;
  }

  public void delete(WebImage wI) {
    repo.delete(wI);
  }

  public List<WebImage> getALl() {
    return repo.findAll();
  }


  public void pushToTop(int id, String category) throws WebImageException {
    List<WebImage> list = repo.findByProductThatHasOrder(category);
    WebImage wI = getById(id);
    wI.setOrderNumber(1);
    repo.save(wI);
    switch (wI.getCategory()) {
      case "banner":
        list.forEach(w -> {
          if (w.getId() != id) {
            if (w.getOrderNumber() > banner) {
              w.setOrderNumber(null);
            } else {
              w.setOrderNumber(w.getOrderNumber() + 1);
            }
            repo.save(w);
          }
        });
        break;
      case "logo":
        list.forEach(w -> {
          if (w.getId() != id) {
            if (w.getOrderNumber() > logo) {
              w.setOrderNumber(null);
            } else {
              w.setOrderNumber(w.getOrderNumber() + 1);
            }
            repo.save(w);
          }
        });
      case "about":
        list.forEach(w -> {
          if (w.getId() != id) {
            if (w.getOrderNumber() > about) {
              w.setOrderNumber(null);
            } else {
              w.setOrderNumber(w.getOrderNumber() + 1);
            }
            repo.save(w);
          }
        });
        break;
    }


  }
}
