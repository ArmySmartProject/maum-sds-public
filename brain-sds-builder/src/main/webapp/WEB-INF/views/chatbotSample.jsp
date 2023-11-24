<html>
<%--                원본 chatbot--%>
<%--                <div id="chatbot">--%>
  <%--                  <button type="button" class="chatbot_refresh" style="display: none;"><em class="text_hide">새로고침</em></button>--%>
  <%--                  <div id="chatbot_iframe_div">--%>
    <%--                    <iframe id="chatbot_iframe" style="width:100%; height:100%"--%>
      <%--                            src = "https://sds.maum.ai/pps_ynbot"--%>
      <%--                            frameborder=0 framespacing=0 marginheight=0 marginwidth=0 scrolling=no vspace=0>--%>
      <%--                    </iframe>--%>
    <%--                  </div>--%>
  <%--                  <div class="blind">--%>
    <%--                    <div>--%>
      <%--                      <p><spring:message code='MESSAGE.CHATBOT.START' text='START를 눌러 대화를<br>시작해주세요.'/></p>--%>
      <%--                      <button type="button" id="chat_test_start" class="btn_primary">START</button>--%>
      <%--                    </div>--%>
    <%--                  </div>--%>
  <%--                </div>--%>
<%--                //원본 chatbot--%>

<%--            수정 chatbot--%>
<%--            [D] AMR 201029 채팅 .chatUI_wrap을 공통으로 사용 --%>

<div id="chatbot">
  <div class="chatUI_wrap">
    <div class="chatUI_mid btmUi scroll">
      <ul class="lst_talk">
        <!-- bot UI -->
        <li class="bot">
          <!-- [D] 기본메세지 -->
          <div class="bot_msg">
            <em class="txt">안녕하세요. 마음AI 챗봇 설리입니다. 무엇을 도와드릴까요?</em>
            <div class="date">2019.08.14 12:00</div>
          </div>
          <!-- [D] 제네릭_URL -->
          <div class="bot_msg">
            <div class="generic">
                                                  <span class="generic_img">
                                                      <img src="https://maum.ai/aiaas/common/images/maum.ai_web.png" alt="마음에이아이">
                                                  </span>
              <span class="generic_url">
                                                      <a href="https://maum.ai" target="_blank">https://mindslab.ai/kr</a>
                                                  </span>
            </div>
            <div class="date">2019.08.14 12:00</div>
          </div>
          <!-- [D] 제네릭_지도 -->
          <div class="bot_msg">
            <div class="generic">
              <span class="generic_img"><img src="//geo0.ggpht.com/cbk?panoid=CtPrxLkShX6I5YMvVRp1pA&amp;output=thumbnail&amp;cb_client=search.LOCAL_UNIVERSAL.gps&amp;thumb=2&amp;w=293&amp;h=79&amp;yaw=98.38457&amp;pitch=0&amp;thumbfov=100" alt="마인즈랩 사진"></span>
              <span class="generic_tit">
                                                      <a class="btn_map" href="#none">마인즈랩</a>
                                                  </span>
              <span class="generic_des">경기도 성남시 분당구 대왕판교로644번길 49 다산타워 6층 601호</span>
            </div>
            <div class="date">2019.08.14 12:00</div>
          </div>
          <!-- [D] 버튼_리스트 -->
          <div class="bot_msg">
            <div class="btnLst">
              <ul>
                <li><a href="#">Option 01</a></li>
                <li><a href="#">Option 02</a></li>
                <li><a href="#">Option 03</a></li>
              </ul>
            </div>
            <div class="date">2019.08.14 12:00</div>
          </div>

          <!-- [D] 버튼_아이템 -->
          <div class="bot_msg">
            <div class="btnItem">
              <ul>
                <li><a href="#">경기도</a></li>
                <li><a href="#">성남시</a></li>
                <li><a href="#">분당구</a></li>
                <li><a href="#">대왕판교</a></li>
                <li><a href="#">다산타워</a></li>
              </ul>
            </div>
            <div class="date">2019.08.14 12:00</div>
          </div>

          <!-- [D] 이미지_가로 -->
          <div class="bot_msg">
            <div class="img">
              <img src="../../images/sample/ttubot.png" alt="뚜봇">
            </div>
            <div class="date">2019.08.14 12:00</div>
          </div>
          <!-- [D] 이미지_세로 -->
          <div class="bot_msg">
            <div class="img">
              <img src="../../images/sample/bg.jpg" alt="뚜봇">
            </div>
            <div class="date">2019.08.14 12:00</div>
          </div>
          <!-- [D] 이미지_ani_가로 -->
          <div class="bot_msg">
            <div class="img">
              <img src="../../images/sample/sample_ani.gif" alt="뚜봇">
            </div>
            <div class="date">2019.08.14 12:00</div>
          </div>
          <!-- [D] 이미지_ani_정사이즈 -->
          <div class="bot_msg">
            <div class="img">
              <img src="../../images/sample/sample_ani02.gif" alt="뚜봇">
            </div>
            <div class="date">2019.08.14 12:00</div>
          </div>
          <!-- [D] 이미지_투명이미지-->
          <div class="bot_msg">
            <div class="img">
              <img src="../../images/sample/sample_s.png" alt="뚜봇">
            </div>
            <div class="date">2019.08.14 12:00</div>
          </div>
          <!-- [D] 텍스트 안 버튼 -->
          <div class="bot_msg">
            <div class="btnLst">
                              <span class="txt">저는 무엇을 물어봐도 다 대답할 수 있어요.
                                  <div class="txt_btn">
                                      <a href="#">자주 묻는 질문</a>
                                  </div>
                              </span>
            </div>
            <div class="date">2019.08.14 12:00</div>
          </div>
          <!-- [D] 텍스트 안 버튼 -->
          <div class="bot_msg">
            <div class="btnLst">
                              <span class="txt">저는 무엇을 물어봐도 다 대답할 수 있어요.
                                  <div class="txt_btns">
                                      <a href="#">버튼1 버튼1 버튼1</a>
                                      <a href="#">버튼2 버튼2</a>
                                      <a href="#">버튼3</a>
                                  </div>
                              </span>
            </div>
            <div class="date">2019.08.14 12:00</div>
          </div>
        </li>
        <!-- //bot UI -->

        <!-- [D] Swiper -->
        <%--                        <li class="botMsg_swiper swiper-container-initialized swiper-container-horizontal">--%>
        <%--                          <div class="swiper-wrapper" style="transform: translate3d(0px, 0px, 0px);">--%>
          <%--                            <div class="swiper-slide swiper-slide-active" style="width: 186.5px; margin-right: 10px;">--%>
            <%--                              <a class="swiper_item" href="#" target="_self">--%>
              <%--                                <span class="item_img"><img src="../../images/sample/sample_swiper01.jpg" alt="이미지 명"></span>--%>
              <%--                                <span class="item_tit">상품 검색할래?</span>--%>
              <%--                                <span class="item_txt">찾고 싶은 상품을 바로 검색하세요.</span>--%>
              <%--                              </a>--%>
            <%--                            </div>--%>
          <%--                            <div class="swiper-slide swiper-slide-next" style="width: 186.5px; margin-right: 10px;">--%>
            <%--                              <a class="swiper_item" href="#" target="_self">--%>
              <%--                                <span class="item_img"><img src="../../images/sample/sample_swiper02.jpg" alt="이미지 명"></span>--%>
              <%--                                <span class="item_tit">주문내역 조회하고 싶어</span>--%>
              <%--                                <span class="item_txt">배송 조회, 주문내역 조회 등 상품 구매와 관련된 질문을 해 주세요.</span>--%>
              <%--                              </a>--%>
            <%--                            </div>--%>
          <%--                            <div class="swiper-slide" style="width: 186.5px; margin-right: 10px;">--%>
            <%--                              <a class="swiper_item" href="#" target="_self">--%>
              <%--                                <span class="item_img"><img src="../../images/sample/sample_swiper03.jpg" alt="이미지 명"></span>--%>
              <%--                                <span class="item_tit">포인트 조회는 어디서 해?</span>--%>
              <%--                                <span class="item_txt">포인트 조회, 포인트 제도에 대해 궁금하신 점을 물어보세요!</span>--%>
              <%--                              </a>--%>
            <%--                            </div>--%>
          <%--                            <div class="swiper-slide" style="width: 186.5px; margin-right: 10px;">--%>
            <%--                              <a class="swiper_item" href="#" target="_self">--%>
              <%--                                <span class="item_img"><img src="../../images/sample/sample_swiper04.jpg" alt="이미지 명"></span>--%>
              <%--                                <span class="item_tit">어플은 어디서 다운받아?</span>--%>
              <%--                                <span class="item_txt">이제너두 전용 앱을 이용하시면 다양한 혜택을 받아보실 수 있습니다.</span>--%>
              <%--                              </a>--%>
            <%--                            </div>--%>
          <%--                            <div class="swiper-slide" style="width: 186.5px; margin-right: 10px;">--%>
            <%--                              <a class="swiper_item" href="#" target="_self">--%>
              <%--                                <span class="item_img"><img src="../../images/sample/sample_swiper05.jpg" alt="이미지 명"></span>--%>
              <%--                                <span class="item_tit">여행 가고싶어!</span>--%>
              <%--                                <span class="item_txt">다양한 여행지의 패키지 상품, 항공권 그리고 호텔을 검색하세요!</span>--%>
              <%--                              </a>--%>
            <%--                            </div>--%>
          <%--                            <div class="swiper-slide" style="width: 186.5px; margin-right: 10px;">--%>
            <%--                              <a class="swiper_item" href="#" target="_self">--%>
              <%--                                <span class="item_img"><img src="../../images/sample/sample_swiper06.jpg" alt="이미지 명"></span>--%>
              <%--                                <span class="item_tit">처음으로</span>--%>
              <%--                                <span class="item_txt">대화중에 처음으로 돌아가고 싶으시면 "처음으로"라고 말해주세요^^</span>--%>
              <%--                              </a>--%>
            <%--                            </div>--%>
          <%--                          </div>--%>
        <%--                          <!-- [D] Swiper Pagination -->--%>
        <%--                          <div class="swiper-pagination swiper-pagination-clickable swiper-pagination-bullets"><span class="swiper-pagination-bullet swiper-pagination-bullet-active" tabindex="0" role="button" aria-label="Go to slide 1"></span><span class="swiper-pagination-bullet" tabindex="0" role="button" aria-label="Go to slide 2"></span><span class="swiper-pagination-bullet" tabindex="0" role="button" aria-label="Go to slide 3"></span><span class="swiper-pagination-bullet" tabindex="0" role="button" aria-label="Go to slide 4"></span><span class="swiper-pagination-bullet" tabindex="0" role="button" aria-label="Go to slide 5"></span></div>--%>
        <%--                          <!-- [D] Swiper navigation buttons -->--%>
        <%--                          <div class="swiper-button-prev swiper-button-disabled" tabindex="0" role="button" aria-label="Previous slide" aria-disabled="true"></div>--%>
        <%--                          <div class="swiper-button-next" tabindex="0" role="button" aria-label="Next slide" aria-disabled="false"></div>--%>
        <%--                          <span class="swiper-notification" aria-live="assertive" aria-atomic="true"></span></li>--%>

        <!-- 사용자 UI -->
        <li class="user">
          <div class="bot_msg">
            <em class="txt">제품을 좀 알아보려고 하는데요.</em>
            <div class="date">2019.08.14 12:00</div>
          </div>
        </li>
        <!-- //사용자 UI -->

        <li class="bot">
          <!-- [D] 기본메세지 -->
          <div class="bot_msg">
            <em class="txt">안녕하세요. 마음AI 챗봇 설리입니다. 무엇을 도와드릴까요?</em>
            <span class="name">name이래</span>
            <div class="date">2019.08.14 12:00</div>
          </div>
        </li>
      </ul>
    </div>
    <!-- .chatUI_btm -->
    <div class="chatUI_btm">
      <form method="post" action="" id="formChat" name="formChat">
        <textarea class="textArea" rows="1" placeholder="메세지를 입력해 주세요"></textarea>
        <input type="submit" name="btn_chat" id="btn_chat" class="btn_chat" title="전송" value="전송">
      </form>
    </div>
    <!-- //#chatUI_btm -->
  </div>
  <button type="button" class="chatbot_refresh"><em class="text_hide"><spring:message code='LABEL.REFRESH' text='새로고침'/></em></button>
  <div class="blind">
    <div>
      <p><spring:message code='MESSAGE.CHATBOT.START' text='START를 눌러 대화를<br>시작해주세요.'/></p>
      <button type="button" id="chat_test_start" class="btn_primary">START</button>
    </div>
  </div>
</div>


<%--                  원본 voicebot--%>
<%--                  <div id="voicebot" class="scroll">--%>
<%--                    <ul class="voice_talk">--%>
<%--                      <li class="bot">--%>
<%--                        <div class="message">--%>
<%--                          <div class="text">--%>
<%--                            안녕하세요. 서비스센터입니다. 무엇을 도와드릴까요?--%>
<%--                          </div>--%>
<%--                        </div>--%>
<%--                        <div class="time"><span class="time">2020.10.27 PM 7:03</span></div>--%>
<%--                      </li>--%>

<%--                      <li class="user">--%>
<%--                        <div class="message">--%>
<%--                          <div class="text">--%>
<%--                            나다--%>
<%--                          </div>--%>
<%--                        </div>--%>
<%--                        <div class="time"><span class="time">2020.10.27 PM 7:03</span></div>--%>
<%--                      </li>--%>
<%--                    </ul>--%>

<%--                    <div class="blind voicebot_start">--%>
<%--                      <div>--%>
<%--                        <p><spring:message code="MESSAGE.VOICEBOT.START" text="상단 입력창에서<br>정보를 입력하신 후<br>전화 걸기를 눌러주세요."/></p>--%>
<%--                      </div>--%>
<%--                    </div>--%>
<%--                  </div>--%>
<%--                  //원본 voicebot--%>

</html>