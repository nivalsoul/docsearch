'use strict';
app.controller('docManagementCtrl', 
        function($scope,$http,$window,$uibModal,docCloudService) {
            var token="";
            $scope.folders=[];
            $scope.loadDoc=function(id){
                docCloudService.loadDoc(token,id)
                    .success(function(resp){
                        if(resp.code==200){
                            $scope.docArr=resp.documents;
                            if($scope.docArr.length > 0){
                                $scope.hasDoc = true;
                            }else{
                                $scope.hasDoc = false;
                            }
                        }else{
                            alert("获取文档列表失败"+resp.message)
                        }

                    }).error(function(resp){
                        alert("获取文档列表出错"+resp.message)
                    })
            };
            $scope.selectFolder=function(folder){
                angular.forEach($scope.folders, function(folder) {
                    folder.selected = false;
                });
                $scope.folder = folder;
                $scope.folder.selected = true;
                var id=folder._id;
                if(id){
                    $scope.loadDoc(id);
                }
            };
            $scope.loadFolder=function(){
                docCloudService.loadFolder(token)
                    .success(function(resp){
                        if(resp.code == 200){
                            $scope.folders=resp.folders;
                            if($scope.folders.length>0){
                            	$scope.selectFolder($scope.folders[0]);
                            }
                        }else{
                            alert("获取文件夹失败"+resp.message)
                        }
                        console.log(resp)
                    }).error(function(resp){
                        alert("获取文件夹失败"+resp.message)
                    })
            };
            $scope.loadFolder();

            //在文件夹下搜索文档
            $scope.searchDoc=function () {
                var q = "category_id:"+$scope.folder._id;
                if($scope.searchInput!=""){
                    q += "@_all:"+$scope.searchInput;
                }
                var para = "q="+q+"&page_num=1&page_size=1000";
                docCloudService.search(token, para)
                    .success(function(resp){
                        if(resp.code==200){
                            $scope.docArr=resp.documents;
                            if($scope.docArr.length > 0){
                                $scope.hasDoc = true;
                            }else{
                                $scope.hasDoc = false;
                            }
                        }else{
                            alert("查询文档列表失败"+resp.message)
                        }
                    }).error(function(resp){
                        alert("查询文档列表出错"+resp.message)
                    })
            };

            $scope.createFolder=function(){
                var data={ "name":"分类名称"};
                $scope.folder={
                    "name":"分类名称",
                    "selected":"",
                    "editing":true,
                    "tag":true
                };
                //$scope.folders.push($scope.folder)
                $scope.folders.splice(0,0,$scope.folder);

                $scope.selectFolder($scope.folder);
            };
            $scope.editFolder=function(folder){
                folder.tag=false;
                folder.editing=true;
            };

            $scope.delFolder=function(id){
                docCloudService.delFolder(token,id)
                    .success(function(resp){
                        if(resp.code==200){
                            alert("删除文件夹成功")
                            $scope.loadFolder();
                        }else{
                            alert("删除文件夹失败"+resp.message)
                        }

                    }).error(function(resp){
                        alert("删除文件夹出错"+resp.message)
                    })

            };

            //创建文件夹
            $scope.doneEditingGroup=function(folder){
                folder.editing=false;
                var data={ "name":folder.name};
                if(folder.tag){
                    docCloudService.createFolder(token,data)
                        .success(function(data){
                            if(data.code==200){
                                alert("新建文件夹成功");
                                $scope.loadFolder();
                            }else{
                                alert("新建文件夹失败"+data.message)
                                $scope.loadFolder();
                            }
                        }).error(function(){
                            alert("新建文件夹出错"+data.message)
                            $scope.loadFolder();
                        });
                    folder.tag="";
                }else{
                    var id=folder._id;
                    docCloudService.editFolder(token,id,data)
                        .success(function(data){
                            if(data.code==200){
                                alert("修改文件夹成功")
                                $scope.loadFolder();
                            }else{
                                alert("修改文件夹失败"+data.message)
                                $scope.loadFolder();
                            }
                        }).error(function(){
                            alert("修改文件夹出错"+data.message)
                            $scope.loadFolder();
                        });
                }

            };

            //新增上传
            $scope.newDoc=function(name,id){
                if($scope.folders.length==0){
                    alert("请先创建文件夹再上传文件！");
                    return;
                }
                $uibModal.open({
                    templateUrl: 'newDoc.html',
                    size:'lg',
                    scope: $scope,
                    keyboard:false,
                    backdrop:'static',
                    controller:'newDocCtrl',
                    resolve: {
                        folder_id:function(){
                            return id;
                        }
                    }
                });
            };

            //编辑文档
            $scope.editDoc = function(doc){
            	$uibModal.open({
                    templateUrl: 'editDoc.html',
                    size:'md',
                    keyboard:false,
                    backdrop:'static',
                    scope: $scope,
                    controller:'editDocCtrl',
                    resolve: {
                        document:function(){
                            return doc;
                        }
                    }
                });
            };

            //删除文档
            $scope.delDoc = function (id) {
                docCloudService.delDoc(token,id)
                    .success(function(resp){
                        if(resp.code==200){
                            alert("删除文档成功")
                            $scope.selectFolder($scope.folder);
                        }else{
                            alert("删除文档失败"+resp.message)
                        }
                    }).error(function(resp){
                        alert("删除文档出错"+resp.message)
                    });
            };

            //重新转换
            $scope.convertFile = function (doc) {
                doc.status='uploaded';
                docCloudService.convertFile(token,doc._id)
                    .success(function(resp){
                        alert(resp.message);
                    }).error(function(resp){
                        alert("转换出错")
                    })
            };
        })
    .controller('newDocCtrl',
        function($scope,$uibModalInstance,folder_id,docCloudService,FileUploader) {
            var token="";
            $scope.fileArr=[];
            $scope.loadTenants=function(){
                docCloudService.loadTenants(token)
                    .success(function(resp){
                        $scope.deptArr=resp;
                        console.log($scope.deptArr);
                        $scope.file.dept_id=$scope.deptArr[0].deptid;
                    })
                    .error(function(){
                        alert("获取租户列表失败")
                    })
            };
            $scope.loadFolder=function(){
                docCloudService.loadFolder(token)
                    .success(function(resp){
                        if(resp.code == 200){
                            $scope.folders=resp.folders;
                        }else{
                            alert("获取文件夹失败"+resp.message)
                        }
                        console.log(resp)
                    }).error(function(resp){
                        alert("获取文件夹列表失败"+resp.message)
                    })
            };
            //文件上传控件
            var uploader = new FileUploader({
                url:"/v1/docdive/documents/file",
                headers: {
                    'X-Auth-Token':token
                }
            });
            $scope.initFile=function(){
                $scope.file={
                    document_id: "",
                    folder_id: folder_id,
                    dept_id: "",
                    title: "",
                    status: "",
                    description: "",
                    uploader: uploader,
                    selected: ""
                };
                $scope.loadTenants();
                $scope.loadFolder();
                $scope.fileUploaded = false;
            };
            $scope.initFile();

            $scope.newUpload=function(){
                uploader.clearQueue();
                $scope.initFile();
                console.log($scope.fileArr);
            };
            $scope.chooseFile=function(obj){
                $scope.file = obj;
                $scope.fileUploaded = true;
            };
            $scope.saveDoc=function(){
                if($scope.fileArr.length==0){
                    alert("请先上传文件！");
                    return;
                }
                var data=[];
                $scope.fileArr.forEach(function(f){
                    var obj = {};
                    obj.document_id = f.document_id;
                    obj.dept_id = f.dept_id;
                    obj.category_id = f.folder_id;
                    obj.title = f.title;
                    obj.description = f.description;
                    data.push(obj);
                });
                console.log(data);
                docCloudService.saveDoc(token,data)
                    .success(function(resp){
                        console.log(resp);
                        if(resp.code == 200){
                            alert("保存成功");
                            $uibModalInstance.close();
                            //上传后刷新列表
                            $scope.loadDoc(folder_id);
                        }else{
                            alert("保存失败:"+resp.message);
                        }
                    })
                    .error(function(resp){
                        alert("保存失败:"+resp.message);
                    })
            };

            $scope.delFile=function (file) {
                docCloudService.delFile(token, file.document_id)
                    .success(function(resp){
                        if(resp.code==200){
                            $scope.fileArr.splice($.inArray(file,$scope.fileArr),1);
                            $scope.newUpload();
                        }else{
                            alert("删除文件失败"+resp.message)
                        }
                    })
                    .error(function(resp){
                        alert("删除文件出错"+resp.message)
                    });
            };
            

            // FILTERS

            uploader.filters.push({
                name: 'customFilter',
                fn: function(item /*{File|FileLikeObject}*/, options) {
                    return this.queue.length < 1;
                }
            });

            // CALLBACKS

            uploader.onWhenAddingFileFailed = function(item /*{File|FileLikeObject}*/, filter, options) {
                console.info('onWhenAddingFileFailed', item, filter, options);
            };
            uploader.onAfterAddingFile = function(fileItem) {
                console.info('onAfterAddingFile', fileItem);
            };
            uploader.onAfterAddingAll = function(addedFileItems) {
                console.info('onAfterAddingAll', addedFileItems);
            };
            uploader.onBeforeUploadItem = function(item) {
                console.info('onBeforeUploadItem', item);
            };
            uploader.onProgressItem = function(fileItem, progress) {
                console.log(uploader.queue);
                console.info('onProgressItem', fileItem, progress);
            };
            uploader.onProgressAll = function(progress) {
                console.info('onProgressAll', progress);
            };
            uploader.onSuccessItem = function(fileItem, response, status, headers) {
                console.info('onSuccessItem', fileItem, response, status, headers);
                if(response.code==200){
                    angular.element("#fileName").focus();
                    $scope.file.title=fileItem.file.name;
                    $scope.file.uploader.queue=fileItem.uploader.queue;
                    $scope.file.status='success';
                    $scope.file.document_id=response.document_id;
                    $scope.fileArr.push($scope.file);//TODO
                    console.log( $scope.fileArr);
                }else{
                    alert("上传文件出错："+response.message);
                    uploader.clearQueue();
                    $scope.initFile();
                }
            };
            uploader.onErrorItem = function(fileItem, response, status, headers) {
                console.info('onErrorItem', fileItem, response, status, headers);
                if(fileItem.isError){
                    uploader.clearQueue();
                    $scope.file.status='error';
                }
                if(status == 413){
                    alert("上传文件出错：文件大小超过了限制！");
                }
            };
            uploader.onCancelItem = function(fileItem, response, status, headers) {
                console.info('onCancelItem', fileItem, response, status, headers);
            };
            uploader.onCompleteItem = function(fileItem, response, status, headers) {
                console.info('onCompleteItem', fileItem, response, status, headers);
            };
            uploader.onCompleteAll = function() {
                console.info('onCompleteAll');
            };
            $scope.cancel = function () {
                $uibModalInstance.close();
            };
        })
    .controller('editDocCtrl',
        function ($scope, $uibModalInstance, document, docCloudService) {
            var token = "";
            $scope.file = {};
            $scope.file._id = document._id;
            $scope.file.dept_id = document.dept_id;
            $scope.file.category_id = document.category_id;
            $scope.file.title = document.title;
            $scope.file.description = document.description;

            $scope.loadTenants = function () {
                docCloudService.loadTenants(token)
                    .success(function (resp) {
                        $scope.deptArr = resp;
                    })
                    .error(function () {
                        alert("获取租户列表失败")
                    })
            };
            $scope.loadFolder = function () {
                docCloudService.loadFolder(token)
                    .success(function (resp) {
                        if (resp.code == 200) {
                            $scope.folders = resp.folders;
                        } else {
                            alert("获取文件夹失败" + resp.message)
                        }
                        console.log(resp)
                    }).error(function (resp) {
                    alert("获取文件夹列表失败" + resp.message)
                })
            };
            $scope.loadTenants();
            $scope.loadFolder();

            $scope.saveDoc = function () {
                var f = $scope.file;
                var data={};
                data._id = f._id;
                data.dept_id = f.dept_id;
                data.category_id = f.category_id;
                data.title = f.title;
                data.description = f.description;
                console.log(data);
                docCloudService.editDoc(token, data)
                    .success(function (resp) {
                        console.log(resp);
                        if (resp.code == 200) {
                            alert("保存成功");
                            $uibModalInstance.close();
                            //上传后刷新列表
                            $scope.selectFolder($scope.folder);
                        } else {
                            alert("保存失败:" + resp.message);
                        }
                    })
                    .error(function (resp) {
                        alert("保存失败:" + resp.message);
                    })

            };
            
            $scope.cancel = function () {
            	$uibModalInstance.close();
            }
        });