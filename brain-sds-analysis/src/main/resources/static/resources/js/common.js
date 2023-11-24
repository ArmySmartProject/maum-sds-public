// MINDsLab. YMJ. 20190830

$(document).ready(function (){

    // 추가 200218 AMR 모바일 메뉴 open
    $('.mobile_menu').on('click', function(){
        $('#wrap').toggleClass('mobile');
        $('body').toggleClass('overflow_hidden');
    });

    // header search
    $('#header .sta .srchBox .ipt_srch').keypress(function (e) {
        if (e.which == 13){
            $('#header .sta .srchBox .btn_ico').click();
        }
    });   
	$('#header .sta .srchBox .btn_ico').on('click',function(){	
        $('.lyrWrap').remove();
        
        //Layer popup 생성 
        $(this).parent().append(' \
            <div class="lyrWrap" tabindex="0" data-tooltip-con="main_srch" data-focus="main_srch" data-focus-prev="main_srch_close"> \
                <div class="lyr_bg"></div> \
                <div class="lyrBox"> \
                    <div class="lyr_top"> \
                        <h3>알림</h3> \
                        <button class="btn_lyr_close">닫기</button> \
                     </div> \
                    <div class="lyr_mid"> \
                        본 화면은 기능개발이 되어 있지 않은 단순 UI만 제공되는 퍼블리싱 파일입니다.<br> \
                        해당 기능 개발 완료 후 common.js에서 Layer popup부분을 모두 삭제하세요. \
                    </div> \
                    <div class="lyr_btm"> \
                        <div class="btnBox sz_small"> \
                            <button class="btn_lyr_close" data-focus="main_srch_close" data-focus-next="main_srch_close">닫기</button> \
                        </div> \
                    </div> \
                </div> \
            </div>'
        );
        //Layer popup 삭제 
        $('.btn_lyr_close, .lyr_bg').on('click',function(){
            $(this).closest('.lyrWrap').addClass('lyr_hide').delay(500).queue(function() { $(this).remove(); });
        });	
	});
    
    
	// header dropdown			   
	$('.btn_dropdown').on('click',function(e){	
		$('.cont_dropdown').hide();
		e.stopPropagation();
		$(this).parent().parent().find('.cont_dropdown').show();
	});
	$(document).on('click',function(){
		$('.cont_dropdown').hide();
	});

	// top button
	$('.btn_goTop').on('click',function(){
		$('html, body').animate({
			scrollTop: 0
		}, 300);
	});

    // 추가 200305 AMR email input open
    $('#set_email').on('click', function(){
        $('#email_input').toggleClass('on');
    });
    $('#email_input .btn_send').on('click', function(){
        $('#email_input').removeClass('on');
    });

	// more button
	$('.dl_tblType01 dl dd .btn_more').on('click',function() {
		$(this).parent().toggleClass('open');
	});	
    
    // select
    $('.selectbox select').on('change',function(){
        var select_name = $(this).children('option:selected').text();
        $(this).siblings('label').text(select_name);
    });
    $('.selectbox select').on('focus',function(){
        $(this).parent().addClass('focus');
    });
    $('.selectbox select').on('blur',function(){
        $(this).parent().removeClass('focus');
    });
    $('.selectbox select:disabled').each(function(){
        $(this).parent().addClass('disabled');
    });
    
    //Radio checked
    $('input[type="radio"]').each(function(){  
         $('.radioBox input[type="radio"]').on('change',function() {
            var radioChecked = $(this).prop('checked');
            
            if (radioChecked) {
                $(this).parent().find('input[type="radio"]').prop('checked', false);  
                $(this).prop('checked', true);
            } 
        });
    });
    
    //profile 설정
	$('#header .sta .ico_profile').on('click',function(){	
        $('.lyrWrap').remove();
        
        //Layer popup 생성 
        $('body').append(' \
            <div class="lyrWrap" tabindex="0" data-tooltip-con="main_srch" data-focus="main_srch" data-focus-prev="main_srch_close"> \
                <div class="lyr_bg"></div> \
                <div class="lyrBox"> \
                    <div class="lyr_top"> \
                        <h3>알림</h3> \
                        <button class="btn_lyr_close">닫기</button> \
                     </div> \
                    <div class="lyr_mid"> \
                        본 화면은 기능개발이 되어 있지 않은 단순 UI만 제공되는 퍼블리싱 파일입니다.<br> \
                        해당 기능 개발 완료 후 common.js에서 profile 설정을 모두 삭제하세요. \
                    </div> \
                    <div class="lyr_btm"> \
                        <div class="btnBox sz_small"> \
                            <button class="btn_lyr_close" data-focus="main_srch_close" data-focus-next="main_srch_close">닫기</button> \
                        </div> \
                    </div> \
                </div> \
            </div>'
        );
        //Layer popup 삭제 
        $('.btn_lyr_close, .lyr_bg').on('click',function(){
            $(this).closest('.lyrWrap').addClass('lyr_hide').delay(500).queue(function() { $(this).remove(); });
        });	
	});
    
    //Codeview Height
    $('.codeview').each(function(){
        var codeviewHeight = $(this).find('code').height();
        
        if ( codeviewHeight > 220) {        
            $(this).append(
                '<button type="button" class="btn_size">&nbsp;</button>'
            );
        }        
    }); 
    
    $('.btn_size').on('click',function(){
        $(this).parent().toggleClass('heightAuto');
    });	
    
    //Font Awesome Codeview    
    $('.btn_lyr_fa').on('click',function(){
        $('.lyrWrap').remove();
        
        var faclassName = $(this).find('span').attr('class')
        //Layer popup 생성 
        $('body').append(' \
            <div class="lyrWrap"> \
                <div class="lyr_bg"></div> \
                <div class="lyrBox"> \
                    <div class="lyr_top"> \
                        <h3>Icon</h3> \
                        <button class="btn_lyr_close">닫기</button> \
                     </div> \
                    <div class="lyr_mid"> \
                        <div class="preview"> \
                            <span class="'+ faclassName +'"></span> \
                        </div> \
                        <div class="codeview"> \
                            <pre>&lt;span class="'+ faclassName +'"&gt;&lt;/span&gt;</pre> \
                        </div> \
                    </div> \
                    <div class="lyr_btm"> \
                        <div class="btnBox sz_small"> \
                            <button class="btn_lyr_close">닫기</button> \
                        </div> \
                    </div> \
                </div> \
            </div>'
        );
        //Layer popup 삭제 
        $('.btn_lyr_close, .lyr_bg').on('click',function(){
            $(this).closest('.lyrWrap').addClass('lyr_hide').delay(500).queue(function() { $(this).remove(); });
        });	
    });	
    
    //window 창 닫기 
    $('.btn_win_close').on('click',function(){
        window.opener='self';
        window.open('','_parent','');
        window.close();
    });	
});	

$(window).scroll(function () {
	var scrollTop = $(window).scrollTop();

	// wide_imgBox scroll animation
	if( scrollTop > 50) {	
		$('#wrap').addClass('fixed');
		$('.btn_goTop').css({
			right: 10,
			opacity: 1,
		}, 100);
	} else {  
		$('#wrap').removeClass('fixed');
		$('.btn_goTop').css({
			right: -50,
			opacity: 0,
		}, 100);
	}
});

// Change the selector if needed
var $table = $('table.scroll'),
    $bodyCells = $table.find('tbody tr:first').children(),
    colWidth;

// Adjust the width of thead cells when window resizes
$(window).resize(function() {
    // Get the tbody columns width array
    colWidth = $bodyCells.map(function() {
        return $(this).width();
    }).get();
    
    // Set the width of thead columns
    $table.find('thead tr').children().each(function(i, v) {
        $(v).width(colWidth[i]);
    });    
}).resize(); // Trigger resize handler
