package com.shopMe.demo.WebImage;

import com.shopMe.demo.Amazon.AmazonS3Util;
import com.shopMe.demo.common.ApiResponse;
import com.shopMe.demo.exceptions.WebImageException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/webImage")
public class WebImageController {

  @Autowired
  private WebImageService webImageService;

  @PostMapping("/add")
  public ResponseEntity<ApiResponse> addImage(@RequestParam String category,
      MultipartFile image) throws IOException {
    if(Objects.isNull(image) || Objects.equals(category, "") || category == null){
      return new ResponseEntity<>(new ApiResponse(false, "Hình ảnh và category không được để trống"),
          HttpStatus.BAD_REQUEST);    }

    WebImage wI = webImageService.addImage(new WebImage(category));
    if (!image.isEmpty()) {
      String fileName = StringUtils.cleanPath(
          Objects.requireNonNull(image.getOriginalFilename()));
      System.out.println(fileName);
      wI.setImage(fileName);
      String uploadDir = "web-images/" + wI.getId();
      AmazonS3Util.removeFolder(uploadDir);
      AmazonS3Util.uploadFile(uploadDir, fileName, image.getInputStream());
    } else {
      if (wI.getImage().isEmpty()) {
        wI.setImage(null);
      }
    }
    webImageService.addImage(wI);
    return ResponseEntity.ok(new ApiResponse(true, "Upload hình ảnh thành công"));
  }

  @PostMapping("/update/{id}")
  public ResponseEntity<ApiResponse> updateImage(@PathVariable("id") Integer id,
      MultipartFile image) throws IOException, WebImageException {
    WebImage wI = webImageService.getById(id);
    if (!image.isEmpty()) {
      String fileName = StringUtils.cleanPath(
          Objects.requireNonNull(image.getOriginalFilename()));
      wI.setImage(fileName);

      String uploadDir = "web-images/" + wI.getId();

      AmazonS3Util.removeFolder(uploadDir);
      AmazonS3Util.uploadFile(uploadDir, fileName, image.getInputStream());
    }
    webImageService.addImage(wI);
    return ResponseEntity.ok(new ApiResponse(true, "Upload hình ảnh thành công"));
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<ApiResponse> delete(@PathVariable("id") Integer id)
      throws IOException, WebImageException {
    WebImage wI = webImageService.getById(id);
    String addressDir = "web-images/" + wI.getId();
    AmazonS3Util.removeFolder(addressDir);
    webImageService.delete(wI);
    return ResponseEntity.ok(new ApiResponse(true, "Xóa hình ảnh thành công"));
  }

  @GetMapping("/")
  public ResponseEntity<List<WebImage>> getByCategory(@RequestParam String category)
      throws WebImageException {
    List<WebImage> list = webImageService.getImage(category);
    return ResponseEntity.ok(list);
  }


  @GetMapping("/getAll")
  public ResponseEntity<List<WebImage>> getAll() {
    List<WebImage> list = webImageService.getALl();
    return ResponseEntity.ok(list);
  }

  @GetMapping("/pushToTop/{id}")
  public ResponseEntity<ApiResponse> pushToTop(
      @RequestParam String category,
      @PathVariable int id) throws WebImageException {
    webImageService.pushToTop(id, category);
    return ResponseEntity.ok(new ApiResponse(true, "Đẩy lên đầu thành công"));
  }
}
