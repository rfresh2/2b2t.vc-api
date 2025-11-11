package vc.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
public class ScalarIndexTransformer extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        var uri = request.getRequestURI();
        if (!"/".equals(uri)) {
            chain.doFilter(request, response);
            return;
        }
        // Wrap response to capture output
        var capture = new ByteArrayOutputStream();
        var capturingResponse = new HttpServletResponseWrapper(response) {
            @Override
            public ServletOutputStream getOutputStream() {
                return new ServletOutputStream() {
                    @Override
                    public boolean isReady() { return true; }
                    @Override
                    public void setWriteListener(WriteListener listener) { }
                    @Override
                    public void write(int b) { capture.write(b); }
                };
            }

            @Override
            public PrintWriter getWriter() {
                return new PrintWriter(capture, true, StandardCharsets.UTF_8);
            }
        };
        chain.doFilter(request, capturingResponse);

        var html = capture.toString(StandardCharsets.UTF_8);
        var modified = html.replace("<title>Scalar API Reference</title>", "<title>2b2t.vc API Explorer</title>");

        var bytes = modified.getBytes(StandardCharsets.UTF_8);
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
    }
}
