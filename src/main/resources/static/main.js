$(document).ready(function () {
	flushRows();
});
function flushRows(){
	$("#dataTable tbody  tr:odd").css("background-color", "#C6E0B4");   
	$("#dataTable tbody  tr:even").css("background-color","#E2EFDA");    
	$("#dataTable tbody  tr:odd").addClass("odd");
	$("#dataTable tbody  tr:even").addClass("even");
	$("#dataTable tbody  tr:odd").find("input").css({"background-color":"#C6E0B4","outline":"none","border": "0"});   
	$("#dataTable tbody  tr:even").find("input").css({"background-color":"#E2EFDA","outline":"none","border": "0"});   
	$("#dataTable tbody  tr:odd").find("input").addClass("odd");
	$("#dataTable tbody  tr:even").find("input").addClass("even");
}
function rowValue(filed,type,comment,defval){
	var len = $('#dataTable tbody tr').length;
	var html = '<tr height="30px" ><td align="center" >'+(len+1)+
	'</td><td><input style="width:100px" value="'+filed+'" /></td><td><input style="width:120px" value="'+type+'" />'+
	'</td><td width="250px"><input height="40px" value="'+comment+'" width="250px"/></td>'+
	'<td><input width="50px" value="'+defval+'"/>'+
	'<td align="center"><input type="checkbox"  checked="checked"/></td>'+
	'<td align="center"><button onclick="delRow(this)" >删除</button></td></tr>';
	$("#dataTable").append(html);
	flushRows();
}
function addTracer(){
	rowValue("CREATED_BY","VARCHAR2(64)","创建人",'');
	rowValue("CREATED_DATE","DATE","创建时间",'sysdate');
	rowValue("UPDATED_BY","VARCHAR2(64)","修改人",'');
	rowValue("UPDATED_DATE","DATE","修改时间",'sysdate');
}

function addRow(){
	rowValue("","","","");
}

function delRow(sss){
	$(sss).parent().parent().remove();
}

function save() {
	var datas = new Array();
	var trs = $("#dataTable tbody").children("tr");
	var len = $("#dataTable tbody tr").length;
	for (var i = 0; i < len; i++) {
		var tds = trs.eq(i).find("td");
		var filedTd = tds.eq(1).find('input').val();
		var typeTd = tds.eq(2).find('input').val();
		var commentTd = tds.eq(3).find('input').val();
		var defValTd = tds.eq(4).find('input').val();
		var allowNullTd = tds.eq(5).find('input').is(':checked');
		var dataRow = {
			filed : filedTd,
			type : typeTd,
			comment : commentTd,
			defVal : defValTd,
			allowNull : allowNullTd
		};
		datas.push(dataRow);
		
	}
	$.ajax({
		url : "http://127.0.0.1:11100/save",
		async : true,
		dataType : "json",
		type: "post",
		data : {
			"datas":JSON.stringify(datas),
			"startNo":$("#startNo").val(),
			"editor":$("#editor").val(),
			"tableName":$("#tableName").val(),
			"tableComment":$("#tableComment").val()
			}
	});
}