'use strict';
angular.module("docSearchApp", []).
controller('docViewCtrl',["$scope", "$sce",'$window', '$location', 'docCloudService',
        function ($scope, $sce, $window, $location, docCloudService) {
            var token = "";
            $scope.docId = $location.search().docId;
            $scope.doc = {};
            $scope.totalPages = $location.search().totalPages;
            $scope.currentPage = $location.search().page;
            console.log($location.search());
            if (!$scope.currentPage) {
                $scope.currentPage = 1;
            }
            if(!$scope.docId){
                $scope.docId = 404;
            }

            $scope.tabIndex=1;
            $scope.fileUrl = "/v1/docdive/documents/";
            $scope.fileName = "";
            $scope.pdfUrl = "";
            $scope.pdfLoaded = false;


            if($location.search().viewType=="text"){//pdf or text
                $scope.tabIndex = 3;
                $scope.txtTab.active=true;
            }

            $scope.getDocument = function (docId) {
                docCloudService.getDocument(token, docId)
                    .success(function (resp) {
                        if (resp.code == 200) {
                            $scope.doc = resp.data;
                            $scope.totalPages = $scope.doc.page_count;
                            $scope.fileName = $scope.doc.file_name;
                            if(!$scope.fileName)
                                $scope.fileName = $scope.doc.title;
                            $scope.pdfUrl = $scope.fileUrl + $scope.docId + "/"
                                + $scope.fileName.replace("."+$scope.doc.format, ".pdf");

                            $scope.showContent();
                        }else {
                            alert("获取文档失败" + resp.message);
                            $scope.pdfURL = $scope.fileUrl + $scope.docId + "/404.pdf";
                            $scope.doc.pages = [{"page":1,"text":"404"}];
                            $scope.totalPages = 1;
                            $scope.showContent();
                        }
                    })
                    .error(function (resp) {
                        alert("获取文档出错" + resp.message);
                    });
            };
            $scope.getDocument($scope.docId);

            $scope.setPage = function (k) {
                $scope.currentPage = Number($scope.currentPage) + k;
                if ($scope.currentPage < 1) {
                    $scope.currentPage = 1;
                }
                if ($scope.currentPage > $scope.totalPages) {
                    $scope.currentPage = $scope.totalPages;
                }
                $scope.showContent();
            };
            
            $scope.goTo=function (e) {
                var keycode = window.event?e.keyCode:e.which;
                if(keycode==13){
                    $scope.showContent();
                }
            }

            $scope.selectPage=function (item) {
                $scope.currentPage = item.page;
                $scope.tabIndex = 1;
                //$scope.showContent();
                $scope.pdfCurrentPage = $scope.currentPage;
            }

            $scope.switchTab = function (i) {
                $scope.tabIndex = i;
                $scope.showContent();
            }

            $scope.showContent=function () {
                if($scope.tabIndex==1){
                    $scope.loadPDF($scope.pdfUrl);
                }else if($scope.tabIndex==2){

                }else if($scope.tabIndex==3){
                    $scope.loadText();
                }
            }


            $scope.loadText = function () {
                $("#pageText").load($scope.fileUrl + $scope.docId + "/p" + $scope.currentPage + ".txt");
            }
            
            $scope.myobj=null;
            
            ///////******angular-pdf-viewer*****//////
            $scope.isLoading = false;
            $scope.downloadProgress = 0;

            $scope.pdfZoomLevels = [];
            $scope.pdfViewerAPI = {};
            $scope.pdfScale = 2;
            $scope.pdfURL = "";
            $scope.pdfFile = null;
            $scope.pdfTotalPages = 0;
            $scope.pdfCurrentPage = 1;
            $scope.pdfSearchTerm = "";
            $scope.pdfSearchResultID = 0;
            $scope.pdfSearchNumOccurences = 0;

            $scope.onPDFProgress = function (operation, state, value, total, message) {
                console.log("onPDFProgress(" + operation + ", " + state + ", " + value + ", " + total + ")");
                if(operation === "render" && value === 1) {
                    if(state === "success") {
                        if($scope.pdfZoomLevels.length === 0) {
                            // Read all the PDF zoom levels in order to populate the combobox...
                            var lastScale = 0.1;
                            do {
                                var curScale = $scope.pdfViewerAPI.getNextZoomInScale(lastScale);
                                if(curScale.value === lastScale) {
                                    break;
                                }

                                $scope.pdfZoomLevels.push(curScale);

                                lastScale = curScale.value;
                            } while(true);
                        }

                        $scope.pdfCurrentPage = 1;
                        $scope.pdfTotalPages = $scope.pdfViewerAPI.getNumPages();
                        $scope.pdfScale = $scope.pdfViewerAPI.getZoomLevel();
                        $scope.isLoading = false;
                    } else {
                        alert("Failed to render 1st page!\n\n" + message);
                        $scope.isLoading = false;
                    }
                } else if(operation === "download" && state === "loading") {
                    $scope.downloadProgress = (value / total) * 100.0;
                } else {
                    if(state === "failed") {
                        alert("Something went really bad!\n\n" + message);
                    }
                }
            };

            $scope.onPDFZoomLevelChanged = function () {
                $scope.pdfViewerAPI.zoomTo($scope.pdfScale);
            };

            $scope.onPDFPageChanged = function () {
                $scope.pdfViewerAPI.goToPage($scope.pdfCurrentPage);
            };

            $scope.zoomIn = function () {
                var nextScale = $scope.pdfViewerAPI.getNextZoomInScale($scope.pdfScale);
                $scope.pdfViewerAPI.zoomTo(nextScale.value);
                $scope.pdfScale = nextScale.value;
            };

            $scope.zoomOut = function () {
                var nextScale = $scope.pdfViewerAPI.getNextZoomOutScale($scope.pdfScale);
                $scope.pdfViewerAPI.zoomTo(nextScale.value);
                $scope.pdfScale = nextScale.value;
            };

            $scope.loadPDF = function (pdfURL) {
                if($scope.pdfURL === pdfURL) {
                   return;
                }

                $scope.isLoading = true;
                $scope.downloadProgress = 0;
                $scope.pdfZoomLevels = [];
                $scope.pdfSearchTerm = "";
                $scope.pdfFile = null;
                $scope.pdfURL = pdfURL;
            };

            $scope.findNext = function () {
                $scope.pdfViewerAPI.findNext();
            };

            $scope.findPrev = function () {
                $scope.pdfViewerAPI.findPrev();
            };

            $scope.onPDFFileChanged = function () {
                $scope.isLoading = true;
                $scope.downloadProgress = 0;
                $scope.pdfZoomLevels = [];
                $scope.pdfSearchTerm = "";

                $scope.$apply(function () {
                    $scope.pdfURL = "";
                    $scope.pdfFile = document.getElementById('file_input').files[0];
                });
            };

            $scope.onPDFPassword = function (reason) {
                return prompt("The selected PDF is password protected. PDF.js reason: " + reason, "");
            };

    		// $scope.printPDF = function () {
            // 	$scope.pdfViewerAPI.print();
            // };

            $scope.trustSrc = function(src) {
                return $sce.trustAsResourceUrl(src);
            };

            $scope.switchToPDF = function (pdfID) {
                // if(pdfID === 0) {
                //     $scope.loadPDF("pdf/demo.pdf");
                // } else if(pdfID === 1) {
                //     $scope.loadPDF("pdf/demo_large.pdf");
                // }
            };
           
            
        }
    ]);