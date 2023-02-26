/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.refill.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import static it.refill.db.Action.createFile_R;
import it.refill.db.Database;
import it.refill.db.Entity;
import it.refill.domain.Allievi;
import it.refill.domain.Attivita;
import it.refill.domain.User;
import it.refill.util.Utility;
import static it.refill.util.Utility.getRequestValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import static java.lang.String.format;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author dolivo
 */
public class OperazioniGeneral extends HttpServlet {

    protected void onlyDownload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getParameter("path");
        File downloadFile = createFile_R(path);
        if (downloadFile != null
                && downloadFile.exists()) {

            try ( FileInputStream inStream = new FileInputStream(downloadFile)) {
                String mimeType = Files.probeContentType(downloadFile.toPath());
                if (mimeType == null) {
                    mimeType = "application/pdf";
                }
                response.setContentType(mimeType);
                String headerKey = "Content-Disposition";
                String headerValue = format("attach; filename=\"%s\"", downloadFile.getName());
                response.setHeader(headerKey, headerValue);
                response.setContentLength(-1);
                try (final OutputStream outStream = response.getOutputStream()) {
                    outStream.write(FileUtils.readFileToByteArray(downloadFile));
                }
//            FileInputStream inStream = new FileInputStream(downloadFile);
//            String mimeType = Files.probeContentType(downloadFile.toPath());
//            if (mimeType == null) {
//                mimeType = "application/pdf";
//            }
//            response.setContentType(mimeType);
//            String headerKey = "Content-Disposition";
//            String headerValue = String.format("attach; filename=\"%s\"", downloadFile.getName());
//            response.setHeader(headerKey, headerValue);
//            OutputStream outStream = response.getOutputStream();
//            byte[] buffer = new byte[4096 * 4096];
//            int bytesRead = -1;
//            while ((bytesRead = inStream.read(buffer)) != -1) {
//                outStream.write(buffer, 0, bytesRead);
//            }
//            inStream.close();
//            outStream.close();
            }
        } else {
            response.sendRedirect("404.jsp");
        }
    }

    protected void showDoc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String path = request.getParameter("path");
        File downloadFile = createFile_R(path);
        if (downloadFile != null && downloadFile.exists()) {
            FileInputStream inStream = new FileInputStream(downloadFile);
            String mimeType = Files.probeContentType(downloadFile.toPath());
            if (mimeType == null) {
                mimeType = "application/pdf";
            }
            response.setContentType(mimeType);
            String headerKey = "Content-Disposition";
            String headerValue = String.format("inline; filename=\"%s\"", downloadFile.getName());
            response.setHeader(headerKey, headerValue);
            OutputStream outStream = response.getOutputStream();
            byte[] buffer = new byte[4096 * 4096];
            int bytesRead = -1;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            inStream.close();
            outStream.close();
        } else {
            User us = (User) request.getSession().getAttribute("user");
            String page = "page/";
            if (us.getTipo() == 1) {
                page += "sa/indexSoggettoAttuatore.jsp";
            } else if (us.getTipo() == 4) {
                page += "ci/indexCi.jsp";
            } else {
                page += "sa/indexMicrocredito.jsp";
            }
            response.sendRedirect("redirect.jsp?page=" + page + "&fileNotFound=true");
        }
    }

    protected void downloadDoc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getParameter("path");
        File downloadFile = createFile_R(path);

        if (downloadFile.exists()) {
            FileInputStream inStream = new FileInputStream(downloadFile);
            String mimeType = Files.probeContentType(downloadFile.toPath());
            if (mimeType == null) {
                mimeType = "application/pdf";
            }
            response.setContentType(mimeType);
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attach; filename=\"%s\"", downloadFile.getName());
            response.setHeader(headerKey, headerValue);
            OutputStream outStream = response.getOutputStream();
            byte[] buffer = new byte[4096 * 4096];
            int bytesRead = -1;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            inStream.close();
            outStream.close();
        } else {
            User us = (User) request.getSession().getAttribute("user");
            String page = "page/";
            if (us.getTipo() == 1) {
                page += "sa/indexSoggettoAttuatore.jsp";
            } else if (us.getTipo() == 4) {
                page += "ci/indexCi.jsp";
            } else {
                page += "sa/indexMicrocredito.jsp";
            }
            response.sendRedirect("redirect.jsp?page=" + page + "&fileNotFound=true");
        }
    }

    protected void excelfad(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String idpr = request.getParameter("idpr");
        Database db1 = new Database(false, false);
        String base64 = db1.getBase64Report(Integer.parseInt(idpr));
        db1.closeDB();

//        String base64 = new ExcelFAD().generatereportFAD_multistanza(idpr);
        if (base64 != null) {
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", new Object[]{"Progetto_" + idpr + "_report_FAD.xlsx"});
            response.setHeader(headerKey, headerValue);
            OutputStream outStream = response.getOutputStream();
            outStream.write(Base64.getDecoder().decode(base64.getBytes()));
            outStream.close();
        } else {
            User us = (User) request.getSession().getAttribute("user");
            String page = "page/";
            if (us.getTipo() == 1) {
                page += "sa/indexSoggettoAttuatore.jsp";
            } else if (us.getTipo() == 4) {
                page += "ci/indexCi.jsp";
            } else {
                page += "sa/indexMicrocredito.jsp";
            }
            response.sendRedirect("redirect.jsp?page=" + page + "&noFileFound=true");
        }

    }

    protected void pdfTob64(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String path = getRequestValue(request, "path");
            byte[] input_file;
            if (path.equals("")) {
                input_file = Files.readAllBytes(Paths.get(((User) request.getSession().getAttribute("user")).getSoggettoAttuatore().getCartaid()));
            } else {
                input_file = Files.readAllBytes(Paths.get(path));
            }

            byte[] encodedBytes = Base64.getEncoder().encode(input_file);
            String encodedString = new String(encodedBytes);
            response.getWriter().write(encodedString);
            response.getWriter().flush();
            response.getWriter().close();

        } catch (Exception e) {
            response.getWriter().write("");
            response.getWriter().flush();
            response.getWriter().close();
        }

    }

    private void ctrlSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        Entity e = new Entity();
        boolean esito = request.getSession().getAttribute("user") == null || e.getPath("mantenance").equals("Y");
        e.close();
        response.getWriter().write(String.valueOf(esito));
        response.getWriter().flush();
        response.getWriter().close();
    }

    private void getAttivita(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        List<Attivita> list = e.getAttivitaValide();
        e.close();

        Map<String, List<Attivita>> out = list.stream().collect(Collectors.groupingBy(a -> a.getComune().getProvincia()));

        response.setContentType("application/json");
        response.setHeader("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(out));
    }

    private void editMailNeet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Entity e = new Entity();
        JsonObject resp = new JsonObject();
        try {
            String idallievo = getRequestValue(request, "idallievo");
            String new_mail = getRequestValue(request, "new_mail");
            e.begin();
            Allievi a = e.getEm().find(Allievi.class, Long.parseLong(idallievo));
            a.setEmail(new_mail);
            e.merge(a);
            e.flush();
            e.commit();
            e.close();
            resp.addProperty("result", true);
            e.insertTracking(String.valueOf(((User) request.getSession().getAttribute("user")).getId()),
                    "OperazioniSA Modifica MAIL discente: " + idallievo + " - " + new_mail);
        } catch (Exception ex) {
            resp.addProperty("result", false);
            resp.addProperty("message", "Errore Mail: " + Utility.estraiEccezione(ex));
        }

        response.getWriter().write(resp.toString());
        response.getWriter().flush();
        response.getWriter().close();

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        if ((User) request.getSession().getAttribute("user") == null) {
            String type = request.getParameter("type");
            if (type != null) {
                switch (type) {
                    case "ctrlSession":
                        ctrlSession(request, response);
                        break;
                    case "showDoc":
                        showDoc(request, response);
                        break;
                    case "onlyDownload":
                        onlyDownload(request, response);
                        break;
                    default:
                        break;
                }
            } else {
                response.sendRedirect("login.jsp");
            }
        } else {
            String type = request.getParameter("type");
            switch (type) {
                case "onlyDownload":
                    onlyDownload(request, response);
                    break;
                case "showDoc":
                    showDoc(request, response);
                    break;
                case "downloadDoc":
                    downloadDoc(request, response);
                    break;
                case "pdfTob64":
                    pdfTob64(request, response);
                    break;
                case "excelfad":
                    excelfad(request, response);
                    break;
                case "ctrlSession":
                    ctrlSession(request, response);
                    break;
                case "getAttivita":
                    getAttivita(request, response);
                    break;
                case "editMailNeet":
                    editMailNeet(request, response);
                    break;
                default:
                    break;
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
