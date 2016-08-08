'use strict';
app.controller('searchCtrl', 
    function($scope,$window,$uibModal,docCloudService) {
        var token="keycloak.token";
        $(window).on('resize',window_resize);
        function window_resize(){
        $scope.contentH=angular.element($window).height()-60;
            angular.element('#leftSearch').css('height',$scope.contentH);
        }
        window_resize();

        $scope.needLogin=function(){
        	$uibModal.open({
                templateUrl: 'login.html',
                size:'md',
                scope: $scope,
                keyboard:false,
                //backdrop:'static',
                controller:'loginCtrl',
                resolve: {
                	
                }
            });
        }
        
        $scope.selectedDept=null;
        $scope.depts=[];
        //文档统计
        $scope.stats=function () {
            docCloudService.stats(token)
                .success(function(resp){
                    if(resp.code==200){
                        $scope.depts=resp.data;
                        $scope.searchByTenant($scope.depts[0]);
                    }else if(resp.code==401){
                    	$scope.needLogin();
                    }else{
                        alert("获取部门列表失败"+resp.message)
                    }

                })
                .error(function(resp){
                	if(resp.code==401){
                    	$scope.needLogin();
                    }else{
                    	alert("获取部门列表出错"+resp.message)
                    }
                })
        };
        $scope.stats();

        //点击部门后查询列表
        $scope.searchByTenant=function(dept){
            angular.forEach($scope.depts,function(item){
              if (dept.dept_id == item.dept_id) {
                  item.selected=true;
              }else{
                  item.selected=false;
              }

            });
            $scope.selectedDept = dept;
            $scope.pageNum=1;
            var cnd = $scope.getQueryCnd();
            $scope.search(cnd);
        };

        $scope.viewType='list';
        $scope.focusInput=false;
        $scope.showFilter=false; //显示过滤菜单

        $scope.pageNum=1;
        $scope.pageSize=10;
        $scope.orderField="created_at";
        $scope.orderBy="asc";
        
        $scope.documents=[];
        $scope.selectedDocs=[];
        $scope.fileUrl="/v1/docdive/documents/";
        $scope.showPages=[];
        $scope.keywords="";
        $scope.showKeywords=false;
        $scope.hasDocs=true;

        //根据参数查询
        $scope.search=function(q){
            console.log(q);
            if($scope.pageNum==1){
                $("#prev").addClass("disabled");
            }else{
                $("#prev").removeClass("disabled");
            }
            var para = "q="+q+"&page_num="+$scope.pageNum+"&page_size="+$scope.pageSize
                +"&order="+$scope.orderField+" "+$scope.orderBy;
            docCloudService.search(token, para)
                .success(function(resp){
                    if(resp.code==200){
                        $scope.documents=resp.documents;
                        angular.forEach($scope.documents,function(item,index){
                            item.index=index;
                            $scope.showPages[index] = [];
                            for(var i=1;i<=item.page_count;i++){
                                $scope.showPages[index].push(i);
                            }
                            //最开始最多显示前三页
                            item.showPageCount = item.pages.length;
                            if(item.showPageCount > 3){
                                item.showPageCount = 3;
                            }
                        });
                        $scope.showKeywords = $scope.keywords!="";
                        var cnt = ($scope.pageNum-1)*$scope.pageSize+$scope.documents.length;
                        var deptCnt = $scope.selectedDept.document_count;
                        if($scope.documents.length<$scope.pageSize || cnt == deptCnt){
                            $("#next").addClass("disabled");
                        }else{
                            $("#next").removeClass("disabled");
                        }
                        if($scope.documents.length==0){
                            $scope.hasDocs = false;
                        }else{
                            $scope.hasDocs = true;
                        }
                    }else if(resp.code==401){
                    	$scope.needLogin();
                    }else{
                        alert("查询文档列表失败"+resp.message)
                    }

                }).error(function(resp){
                	if(resp.code==401){
                    	$scope.needLogin();
                    }else{
                    	alert("查询文档列表出错"+resp.message)
                    }
                })
        };

        //输入框回车搜索
        $scope.searchByCnd=function(e){
            $scope.showFilter=false;
            var keycode = window.event?e.keyCode:e.which;
            if(keycode==13){
                $scope.pageNum=1;
                var cnd = $scope.getQueryCnd();
                $scope.search(cnd);
            }
        };

        $scope.getQueryCnd=function(){
            var all="";
            var cnd="";
            if($scope.selectedDept){
                cnd += "dept_id:"+$scope.selectedDept.dept_id+"@";
            }
            angular.forEach($scope.inputTagArr,function(item,index){
                if(item.value!=""){
                    if(item.key=="text"){
                        all += item.value+"@";
                    }else{
                        cnd += item.key+":"+item.value+"@";
                    }
                }
            });
            if($scope.mainInput && $scope.mainInput!=""){
                all += $scope.mainInput.trim().replace(/ /g, "@");
                $scope.inputTagArr.push(
                    {
                        "key":"text",
                        id:"input" + (i++),
                        label:"内容",
                        value:$scope.mainInput
                    }
                );
                $scope.mainInput="";
            }
            if(all != ""){
                cnd += all;
                $scope.keywords = "<font color='red'>“"
                    +all.replace(/@/g, " ").trim()+"<font color='red'>”";
            }else{
                cnd = cnd.substring(0, cnd.length-1);
                $scope.keywords = "";
            }
            return cnd;
        };

        $scope.setOrder=function (field,e,o) {
            if($scope.orderField==field){
                if($scope.orderBy=="asc"){
                    $scope.orderBy="desc";
                }else{
                    $scope.orderBy="asc";
                }
            }else{
                $scope.orderField = field;
                $scope.orderBy="asc";
            }
            $("#orderSelect").find("li").each(function(){
                $(this).removeClass("active");
            });
            var obj = e.target || e.srcElement;
            console.log(obj.nodeName);
            var tName = obj.tagName || obj.nodeName;
            while(tName.toUpperCase() != 'A'){
                obj = $(obj).parent()[0];
                tName = obj.tagName || obj.nodeName;
            }
            $(obj).parent().addClass("active");
            $("#orderIcon").remove();
            var ss = "glyphicon-arrow-up";
            if($scope.orderBy == "desc")
                ss = "glyphicon-arrow-down";
            $(obj).append('<span id="orderIcon" class="glyphicon '+ss+'"></span>');
            //点击排序后重新查询
            var cnd = $scope.getQueryCnd();
            $scope.search(cnd);
        }

        $scope.setPage=function (k,e) {
            var obj = e.target || e.srcElement;
            if($(obj).parent().hasClass("disabled")){
                return;
            }
            $scope.pageNum = $scope.pageNum + k;
            if ($scope.pageNum < 1) {
                $scope.pageNum = 1;
            }
            if ($scope.pageNum > 10) {
                $scope.pageNum = 10;
            }
            var cnd = $scope.getQueryCnd();
            $scope.search(cnd);
        }

        //切换显示全部页面的图片
        $scope.switchPages=function(k,id,count){
            if($("#docPages-"+id).css("display")=="none"){
                $("#docPages-"+id).show();
                $("#docPages-"+id).find("img").each(function(){
                    var imgId = $(this).attr("id");
                    var p = imgId.split("_")[1];
                    $(this).attr("src", $scope.fileUrl+id+'/thumb_p'+p+'.png');
                });
            }else{
                $("#docPages-"+id).hide();
            }
        };
        $scope.hidePages=function(id){
            $("#docPages-"+id).hide();
        };

        //显示文档关键词页面
        $scope.showAllKeywordsPage=function(doc){
            doc.showPageCount = doc.pages.length;
        };
        
        //选中文档
        $scope.selectDoc=function(doc){
            //将来支持ctrl多选，目前只支持选择一个
            angular.forEach($scope.selectedDocs,function (item) {
                item.selected = false;
            })
            doc.selected=true;
            $scope.selectedDocs = [];
            if($.inArray(doc,$scope.selectedDocs) == -1){
                $scope.selectedDocs.push(doc);
            }
        };

        //查看文档详情
        $scope.viewDoc=function(doc, viewType, p){
            if(!doc){
                return;
            }
            var url = 'docView.html?docId='+doc._id;
            if(viewType){
                url += '&viewType='+viewType;
            }
            if(p){
                url += '&page='+p;
            }
            window.open(url, '_blank');
        };

        $scope.inputTagArr=[];
        var i=0;
        $scope.showMainInput = true;
        $scope.chooseKey=function(key,label){
            if($scope.mainInput && $scope.mainInput!=""){
                i++;
                $scope.inputTagArr.push(
                    {
                        "key":"text",
                        id:"input"+i,
                        label:"内容",
                        value:$scope.mainInput
                    }
                );
                $scope.mainInput="";
            }
            i++;
            $scope.inputTag={
                "key":key,
                id:"input"+i,
                label:label,
                value:""
            };
            $scope.inputTagArr.push($scope.inputTag);
            $scope.selectLabel($scope.inputTag);
            $scope.showMainInput = false;
        };
        $scope.removeTag=function(id){
            angular.forEach($scope.inputTagArr,function(item,index){
                if(item.id==id){
                    $scope.inputTagArr.splice(index,1)
                }
            });
            $scope.setInputWidth();
            var cnd = $scope.getQueryCnd();
            $scope.search(cnd);
        };
        $scope.clearTags=function(){
            $scope.inputTagArr=[];
            $scope.mainInput='';
            $scope.setInputWidth();
            var cnd = $scope.getQueryCnd();
            $scope.search(cnd);
        }

        $scope.selectLabel=function(item){
            angular.forEach($scope.inputTagArr,function(ele){
                ele.selected=false;
            });
            item.selected=true;
        };
        $scope.blurLabel=function(){
            angular.forEach($scope.inputTagArr,function(ele){
                ele.selected=false;
            });
            $scope.showMainInput = true;
            $scope.setInputWidth();
        };
        $scope.getWidth=function(value,e){
            /*$scope.value=value;
            $scope.$watch('value',function(newVal, oldVal, scope){
                if (newVal === oldVal) {
                    //angular.element(event.target).css("width",20)
                }else{
                    angular.element(e.target)
                        .css("width",angular.element(e.target).next().width()+10);
                }
            });*/
            $scope.setInputWidth();
            //回车搜索
            var keycode = window.event?e.keyCode:e.which;
            if(keycode==13){
                $scope.pageNum=1;
                var cnd = $scope.getQueryCnd();
                $scope.search(cnd);
            }
        };

        //tag输入框自适应宽度
        $scope.changeLen=function (id) {
            var value = $("#"+id).val();
            $("#"+id).css('width', $scope.textWidth(value));
        }
        //获取文本宽度
        $scope.textWidth=function(text){
            var sensor = $('<pre>'+ text +'</pre>').css({display: 'none'});
            $('body').append(sensor);
            var width = sensor.width();
            sensor.remove();
            width += 8;
            if(width < 40)
                width = 40;
            return width;
        };

        $scope.setInputWidth=function () {
            var w = 0;
            var tags = $("div.searchTag");
            if(tags.length > 0){
                var lastTag = tags[tags.length-1];
                var oc = $(lastTag).offset().left + $(lastTag).width();
                var op = $(lastTag).parent().offset().left + $(lastTag).parent().width();
                w = op -oc -w -50;
            }
            if(w < 100){
                w = "98%";
            }
            $("#mainInput").css("width", w);
        }
        
        $scope.downloadFile=function () {
            var d = $scope.selectedDocs[0];
            window.open($scope.fileUrl+d._id+'/'+d.file_name, "_blank");
        }

        //响应点击事件
        $scope.searchPageClick = function(e){
            var elem = e.target || e.srcElement;
            if (elem && "mainInput" == elem.id) {
                $scope.inputIsFocus=true;
                $scope.showFilter=true;
            }else{
                $scope.inputIsFocus=false;
                $scope.showFilter=false;
            }
        };

        $scope.inputFocus = function(){
            var left = document.getElementById("mainInput").offsetLeft;
            angular.element(".filterBox").css("left",left+190);
        };
        
        $scope.myclick=function(){
        	//$("#mainInput").click();
        }

    })
 