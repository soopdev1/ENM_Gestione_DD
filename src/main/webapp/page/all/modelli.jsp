<%-- 
    Document   : modelli
    Created on : 2-mar-2020, 12.41.15
    Author     : agodino
--%>
<%@page import="it.refill.db.Action"%>
<%@page import="it.refill.domain.Cloud"%>
<%@page import="java.util.List"%>
<%@page import="it.refill.db.Entity"%>
<%  Entity e = new Entity();
    List<Cloud> clouds = e.getCloudFilesByTipo(tipo);/*e.findAll(Cloud.class)*/;
    e.close();
%>
<div class="row">
    <%for (Cloud c : clouds) {
    if (Action.isModifiable(c.getVisible(), String.valueOf(us.getTipo()))) {%>
    <div class='col-xl-3 col-lg-6 col-md-6 col-sm-12 form-group'>
        <div class='row'>
            <div class='col-4 paddig_0_r' style="text-align: center;">
                <a href='' class='btn-icon kt-font-io' onclick="return false;">
                    <i class='fa fa-file-pdf' style='font-size: 100px;'></i>
                </a>
            </div>
            <div class='col-1 paddig_0_l' style='text-align: left;'>
                <input type="hidden" value="<%=request.getContextPath()%>/OperazioniGeneral?type=downloadDoc&path=<%=c.getPath().replaceAll("\\\\", "/")%>">
                <a class="btn btn-icon btn-sm btn-io-n download" href="javascript:void(0);" onclick="" data-container="body" data-html="true" data-toggle="kt-tooltip" title="Scarica documento">
                    <i class="fa fa-arrow-down"></i>
                </a>
            </div>
            <%if(us.getTipo() == 2) {%>
            <div class='col-1 paddig_0_l' style='text-align: left;'>
                <input type="hidden" value="<%=c.getId()%>">
                <a class="btn btn-icon btn-sm btn-danger delete" href="javascript:void(0);" onclick="" data-container="body" data-html="true" data-toggle="kt-tooltip" title="Elimina documento">
                    <i class="fa fa-times"></i>
                </a>
            </div>
            <%}%>    
        </div>
        <div class='row'>
            <h5 class='kt-font-io-n'><%=c.getNome().replaceAll("_", " ")%></h5>
        </div>
    </div>
    <%}}%>
</div>
<script>
    $("a.document").click(function (e) {
        var input = $($($(this).parent()[0]).find('input')[0]);
        clickLink(input.val(), "_blank");
    });
    $("a.download").click(function (e) {
        var input = $($($(this).parent()[0]).find('input')[0]);
        console.log(input.val());
        clickLink(input.val(), "");
    });
    
    
    function deleteDocCloud(id) {
    swal.fire({
        title: '<h3 class="kt-font-io-n"><b>Conferma Eliminazione</b></h3><br>',
        html: "<h5 style='text-align:center;'>Sicuro di voler eliminare questo documento?</h5>",
        animation: false,
        showCancelButton: true,
        confirmButtonText: '&nbsp;<i class="la la-check"></i>',
        cancelButtonText: '&nbsp;<i class="la la-close"></i>',
        cancelButtonClass: "btn btn-io-n",
        confirmButtonClass: "btn btn-io",
        customClass: {
            popup: 'large-swal animated bounceInUp',
        },
    }).then((result) => {
        if (result.value) {
            $.ajax({
                type: "POST",
                url: '<%=request.getContextPath()%>/OperazioniMicro?type=deleteDocCloud&id=' + id,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeSwal();
                    var json = JSON.parse(data);
                    if (json.result) {
                        swalSuccessReload("Documento Eliminato", "Operazione eseguita con successo.");
                    } else {
                        swalError("Errore", json.message);
                    }
                },
                error: function () {
                    swalError("Errore", "Non è stato possibile eliminare il documento");
                }
            });
        } else {
            swal.close();
        }
    });
}
    $("a.delete").click(function (){
         deleteDocCloud($($($(this).parent()[0]).find('input')[0]).val());
    });
    function clickLink(link, target) {
        var a = document.createElement('a');
        a.href = link;
        a.target = target;
        document.body.appendChild(a);
        a.click();
        a.remove();
    }
</script>