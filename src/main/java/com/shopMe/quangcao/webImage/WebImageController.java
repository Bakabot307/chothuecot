package com.shopMe.quangcao.webImage;

import com.shopMe.quangcao.amazon.AmazonS3Util;
import com.shopMe.quangcao.common.ApiResponse;
import com.shopMe.quangcao.common.FileUploadUtil;
import com.shopMe.quangcao.exceptions.WebImageException;
import java.io.IOException;
import java.util.Comparator;
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
    if (Objects.isNull(image) || Objects.equals(category, "") || category == null) {
      return new ResponseEntity<>(new ApiResponse(false, "Hình ảnh và category không được để trống"),
          HttpStatus.BAD_REQUEST);
    }

    WebImage wI = webImageService.addImage(new WebImage(category));
    if (!image.isEmpty()) {
      String fileName = StringUtils.cleanPath(
          Objects.requireNonNull(image.getOriginalFilename()));
      wI.setImage(fileName);
      String uploadDir = "web-images/" + wI.getId();
      FileUploadUtil.cleanDir(uploadDir);
      FileUploadUtil.saveFile(uploadDir, fileName, image);
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
      @RequestParam boolean active,
      MultipartFile image) throws IOException, WebImageException {
    if (image == null) {
      return new ResponseEntity<>(new ApiResponse(false, "Hình ảnh không được để trống"),
          HttpStatus.BAD_REQUEST);
    }
    WebImage wI = webImageService.getById(id);

    if (!image.isEmpty()) {
      String fileName = StringUtils.cleanPath(
          Objects.requireNonNull(image.getOriginalFilename()));
      wI.setImage(fileName);
      String uploadDir = "web-images/" + wI.getId();
      FileUploadUtil.cleanDir(uploadDir);
      FileUploadUtil.saveFile(uploadDir, fileName, image);
    }
    wI.setActive(active);
    webImageService.update(wI);
    return ResponseEntity.ok(new ApiResponse(true, "Upload hình ảnh thành công"));
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<ApiResponse> delete(@PathVariable("id") Integer id)
      throws IOException, WebImageException {
    WebImage wI = webImageService.getById(id);
    String addressDir = "web-images/" + wI.getId();
    FileUploadUtil.removeDir(addressDir);
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
    List<WebImage> list = webImageService.getALl().stream().sorted(Comparator.comparing(WebImage::getCategory))
        .toList();
    return ResponseEntity.ok(list);
  }
}
