package ajp.app.controller;

import ajp.app.model.BaseBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class MainController {

    @GetMapping(path = "/main")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseBean> get(@RequestParam(name = "id", required = false) Integer id) {
        log.info("GET:: /main");
        return ResponseEntity.ok(new BaseBean(1, "arbe", "active"));
    }

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BaseBean> health() {
        log.info("GET:: /main");
        return ResponseEntity.ok(new BaseBean(1, "arbe", "active"));
    }

}
