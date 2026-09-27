    package com.gerenciador.estoque.exception;


    import com.gerenciador.estoque.domain.dto.ErroResponse;
    import jakarta.servlet.http.HttpServletRequest;
    import org.springframework.context.support.DefaultMessageSourceResolvable;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.MethodArgumentNotValidException;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import org.springframework.web.bind.annotation.RestControllerAdvice;

    import java.util.stream.Collectors;

    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(RegistroNaoLocalizadoException.class)
        public ResponseEntity<ErroResponse> handleNotFound(RegistroNaoLocalizadoException ex, HttpServletRequest request) {
            ErroResponse erro = new ErroResponse(
                    ex.getMessage(),
                    HttpStatus.NOT_FOUND.value(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
        }

        @ExceptionHandler({EntradaInvalidaException.class, EstoqueInsuficienteException.class})
        public ResponseEntity<ErroResponse> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
            ErroResponse erro = new ErroResponse(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST.value(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErroResponse> handleGeneric(Exception ex,  HttpServletRequest request) {
            String mensagem = "Erro interno no servidor: " + ex.getMessage();
            ErroResponse erro = new ErroResponse(
                    mensagem,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErroResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                                       HttpServletRequest request) {
            String mensagem = ex.getBindingResult().getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            ErroResponse erro = new ErroResponse(
                    "Erro de validação: " + mensagem,
                    HttpStatus.BAD_REQUEST.value(),
                    request.getRequestURI()
            );
            return ResponseEntity.badRequest().body(erro);
        }
    }