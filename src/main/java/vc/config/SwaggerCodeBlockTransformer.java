package vc.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class SwaggerCodeBlockTransformer extends SwaggerIndexPageTransformer {
    public SwaggerCodeBlockTransformer(final SwaggerUiConfigProperties swaggerUiConfig,
                                       final SwaggerUiOAuthProperties swaggerUiOAuthProperties,
                                       final SwaggerUiConfigParameters swaggerUiConfigParameters,
                                       final SwaggerWelcomeCommon swaggerWelcomeCommon,
                                       final ObjectMapperProvider objectMapperProvider) {
        super(swaggerUiConfig,
              swaggerUiOAuthProperties,
              swaggerUiConfigParameters,
              swaggerWelcomeCommon,
              objectMapperProvider);
    }

    @Override
    public Resource transform(HttpServletRequest request,
                              Resource resource,
                              ResourceTransformerChain transformer) throws IOException {
        if (resource.toString().contains("index.html")) {
            final InputStream is = resource.getInputStream();
            final InputStreamReader isr = new InputStreamReader(is);
            try (BufferedReader br = new BufferedReader(isr)) {
                final List<String> lines = br.lines().collect(Collectors.toList());
                int endHeadIndex = -1;
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).contains("</head>")) {
                        endHeadIndex = i;
                        break;
                    }
                }
                if (endHeadIndex != -1) {
                    lines.add(endHeadIndex, "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../swagger-theme/theme-material.css\" />");
                }
                return new TransformedResource(resource, String.join("\n", lines).getBytes());
            } // AutoCloseable br > isr > is
        }
        return super.transform(request, resource, transformer);
    }
}
