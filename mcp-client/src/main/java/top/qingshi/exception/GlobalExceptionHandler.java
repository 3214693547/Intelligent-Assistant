package top.qingshi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.qingshi.utils.LeeResult;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthException.class)
    public LeeResult handleAuthException(AuthException e) {
        log.error("认证异常: {}", e.getMessage());
        return LeeResult.errorMsg(e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public LeeResult handleValidException(Exception e) {
        String message = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException ex) {
            if (ex.getBindingResult().hasErrors()) {
                message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            }
        } else if (e instanceof BindException ex) {
            if (ex.getBindingResult().hasErrors()) {
                message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            }
        }
        log.error("参数校验异常: {}", message);
        return LeeResult.errorMsg(message);
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(RuntimeException.class)
    public LeeResult handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return LeeResult.errorMsg(e.getMessage());
    }

    /**
     * 处理所有异常
     */
    @ExceptionHandler(Exception.class)
    public LeeResult handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return LeeResult.errorMsg("系统异常，请联系管理员");
    }
}
