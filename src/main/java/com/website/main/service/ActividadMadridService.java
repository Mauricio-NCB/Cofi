package com.website.main.service;

import com.website.main.dto.ActividadMadrid.ActividadMadridDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActividadMadridService {

    private static final String URL_MADRID =
        "https://datos.madrid.es/egob/catalogo/206974-0-agenda-eventos-culturales-100.xml";
    private List<ActividadMadridDTO> cache = null;
    private long ultimaDescarga = 0;
    private static final long TTL = 1000 * 60 * 60; // 1 hora en milisegundos

    public List<ActividadMadridDTO> obtenerActividades() {
        long ahora = System.currentTimeMillis();
        if (cache == null || ahora - ultimaDescarga > TTL) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                String xml = restTemplate.getForObject(URL_MADRID, String.class);
                cache = parsearXml(xml);
                ultimaDescarga = ahora;
            } catch (Exception e) {
                return cache != null ? cache : new ArrayList<>();
            }
        }
        return cache;
    }

    private List<ActividadMadridDTO> parsearXml(String xml) throws Exception {
        List<ActividadMadridDTO> actividades = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(
            new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))
        );

        NodeList contenidos = doc.getElementsByTagName("contenido");

        for (int i = 0; i < contenidos.getLength(); i++) {
            Element contenido = (Element) contenidos.item(i);
            Element atributosEl = (Element) contenido.getElementsByTagName("atributos").item(0);

            if (atributosEl == null) continue;

            // extraer atributos a un mapa
            java.util.Map<String, String> datos = new java.util.HashMap<>();
            NodeList atributos = atributosEl.getChildNodes();

            for (int j = 0; j < atributos.getLength(); j++) {
                Node node = atributos.item(j);
                if (node.getNodeType() != Node.ELEMENT_NODE) continue;
                Element atributo = (Element) node;
                String nombre = atributo.getAttribute("nombre");

                // LOCALIZACION es anidada
                if ("LOCALIZACION".equals(nombre)) {
                    NodeList subs = atributo.getChildNodes();
                    for (int k = 0; k < subs.getLength(); k++) {
                        Node subNode = subs.item(k);
                        if (subNode.getNodeType() != Node.ELEMENT_NODE) continue;
                        Element sub = (Element) subNode;
                        datos.put(sub.getAttribute("nombre"),
                                  sub.getTextContent().trim());
                    }
                } else {
                    datos.put(nombre, atributo.getTextContent().trim());
                }
            }

            ActividadMadridDTO dto = new ActividadMadridDTO();
            dto.setId(datos.getOrDefault("ID-EVENTO", ""));
            dto.setTitulo(datos.getOrDefault("TITULO", "Sin título"));
            dto.setDescripcion(datos.getOrDefault("DESCRIPCION", ""));
            dto.setFechaInicio(formatearFecha(datos.get("FECHA-EVENTO")));
            dto.setFechaFin(formatearFecha(datos.get("FECHA-FIN-EVENTO")));
            dto.setHora(datos.get("HORA-EVENTO"));
            dto.setLugar(datos.get("NOMBRE-INSTALACION"));
            dto.setDireccion(datos.get("DIRECCION-INSTALACION"));
            dto.setDistrito(datos.get("DISTRITO"));
            dto.setBarrio(datos.get("BARRIO"));
            dto.setUrlInfo(datos.get("CONTENT-URL"));
            dto.setGratuita("1".equals(datos.get("GRATUITO")));
            dto.setPrecio(datos.get("PRECIO"));

            String tipo = datos.get("TIPO");
            if (tipo != null && tipo.contains("/")) {
                dto.setTipo(tipo.substring(tipo.lastIndexOf("/") + 1));
            } else {
                dto.setTipo(tipo);
            }

            actividades.add(dto);
        }

        return actividades;
    }

    private String formatearFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;
        // "2026-03-03 00:00:00.0" → "03/03/2026"
        try {
            String[] partes = fecha.split(" ")[0].split("-");
            return partes[2] + "/" + partes[1] + "/" + partes[0];
        } catch (Exception e) {
            return fecha;
        }
    }
}