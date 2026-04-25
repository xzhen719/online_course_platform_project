-- 1. Users --DONE
-- STUDENT (password: 123456)
INSERT IGNORE INTO users (id, email, password, username, bio, role, created_at, updated_at)
VALUES (1, 'ocpstudent01@gmail.com', '$2a$10$UhSy/0jEf0MPej7cu7TBUefjscDOnc4IgLrQunR6FKj.7hsAkT606', 'Jack Chou', 'I''m just a nobody.', 'STUDENT', NOW(), NOW());

-- INSTRUCTOR (password: 123456)
INSERT IGNORE INTO users (id, email, password, username, bio, role, created_at, updated_at)
VALUES (2, 'andy@gmail.com', '$2a$10$zXkDvKaouo.DdWqQU5oj.Oj3Y5sr8Quex2.mY0.UpeqkhE7Hh5EB.', 'Andy Wu', '大家好，我是Andy老師', 'INSTRUCTOR', NOW(), NOW());

-- INSTRUCTOR (password: 123456)
INSERT IGNORE INTO users (id, email, password, username, role, created_at, updated_at)
VALUES (3, 'kat@gmail.com', '$2a$10$ksyHtnCbdm9Tv3sq.MV5P.dK0QlMDVpHQA5R9s3cyqaa04THe4.k.', 'Kat Lin', 'INSTRUCTOR', NOW(), NOW());

-- INSTRUCTOR (password: 123456)
INSERT IGNORE INTO users (id, email, password, username, role, created_at, updated_at)
VALUES (4, 'chris@gmail.com', '$2a$10$3H.4OIQie4xWXxBAlkBZgeyEHTwoCYK8vLqjMCjt5G/quxTYI5u4K', 'Chris Wong', 'INSTRUCTOR', NOW(), NOW());


-- 2. Categories --DONE
INSERT IGNORE INTO categories (id, name) VALUES (1, '自然');
INSERT IGNORE INTO categories (id, name) VALUES (2, '數學');
INSERT IGNORE INTO categories (id, name) VALUES (3, '社會');
INSERT IGNORE INTO categories (id, name) VALUES (4, '金融');
INSERT IGNORE INTO categories (id, name) VALUES (5, '歷史');
INSERT IGNORE INTO categories (id, name) VALUES (6, '商業');
INSERT IGNORE INTO categories (id, name) VALUES (7, '其他');


-- 3. Courses
-- Course 1
INSERT IGNORE INTO courses (id, name, description, price, instructor_name, user_id, category_id, status, rating_average, student_count, rating_count, total_time, image_url, video_url, created_at, updated_at)
VALUES (1,
        '基礎物理(一)',
        '你是否曾經好奇：為什麼球會落地？為什麼雲會飄、船能浮、聲音能傳到我們耳中？這些看似平常的現象，其實背後都有著令人著迷的物理規律。在這堂物理課程中，我們不會只是背公式、記定義，而是從生活出發，一起探索世界如何運作。你將學會用最簡單的方式理解運動、力量、能量與波動等核心概念，並發現它們如何影響你每天的生活——從滑滑板的加速度、手機的聲音傳遞，到海浪、光影的變化，原來都能用物理來解釋！課程中，我們會引導你觀察自然界的「模式」，並學習如何將這些模式整理成模型，用來預測事件、理解現象。你將逐步培養科學思考能力，訓練自己像小小科學家一樣提出問題、驗證想法、找到答案。這不是一門枯燥的課，而是一段探索宇宙規律的冒險旅程。只要願意保持好奇心，你會發現物理其實是一種理解世界的超能力。讓我們一起用更有趣、更貼近生活的方式，揭開自然界的神秘面紗吧！',
        3000, 'Andy Wu', 2, 1, 'ON_SHELF', 4.5, 98, 123, 112,
        'http://localhost:8080/images/physics.png',
        'https://www.youtube.com/watch?v=79FTQY9LoQU&list=PLSQl0a2vh4HC-daPugvP8kL_zuE3u_3Zr', NOW(), NOW());

-- Course 2
INSERT IGNORE INTO courses (id, name, description, price, instructor_name, user_id, category_id, status, rating_average, student_count, rating_count, total_time, image_url, video_url, created_at, updated_at)
VALUES (2,
        '生活處處有化學',
        '你身邊的一切——水杯、空氣、手機、光、甚至你自己——都是由物質所構成，而「化學」就是帶你拆解這些物質背後秘密的學問。從古代煉金術士試圖將鉛變成黃金，到現代科學家利用原子打造新材料，人類一直在尋找自然世界的「組成密碼」。這門課將帶你回到物質最核心的地方：原子。你會認識它們如何彼此吸引、結合、分裂，並因此形成千變萬化的物質世界。你也會看到，物質並不永遠保持原樣，它會腐蝕、生鏽、燃燒、爆炸、轉變，而造成這些改變的力量，就是能量。你的任務不只是記住方程式，而是理解變化背後的原因：為什麼鐵會生鏽？為什麼糖加熱會變成焦糖？為什麼炸藥能瞬間釋放巨大能量？這些現象之所以會發生，都是因為粒子之間的排列與能量的重新分配。課程中，你將從微觀的原子世界一路探索到宏觀的物質變化，理解能量如何驅動反應、物質如何轉換、宇宙又如何因此更有秩序。如果你曾經好奇「世界是怎麼被組合起來的？」，這堂課會帶你找到答案。化學不是一堆枯燥的符號，而是一段理解宇宙運作方式的旅程',
        2500, 'Kat Lin', 3, 1, 'ON_SHELF', 4.3, 322, 154, 98,
        'http://localhost:8080/images/chemistry.png',
        'https://www.youtube.com/watch?v=gGalnVpaom8&list=PLSQl0a2vh4HBnhjPgsJU2y1UhMwcYmb5u', NOW()    , NOW());

-- Course 3
INSERT IGNORE INTO courses (id, name, description, price, instructor_name, user_id, category_id, status, rating_average, student_count, rating_count, total_time, image_url, video_url, created_at, updated_at)
VALUES (3,
        '你不能錯過的代數課',
        '代數聽起來似乎遙遠又抽象，但其實它就是一種「用符號描述世界規律的語言」。在這門課程《代數基礎》中，我們將從最根本的起點，帶你踏上理解代數的旅程。你會從代數的歷史開始了解：它是如何誕生的？古代數學家如何用符號簡化複雜問題，並為現代科學奠定基礎。接著，我們將引導你學會評估代數式：如何把帶有變數的式子轉化為具體數值，理解變數的意義。這部分會讓你發現，代數並非空洞的符號，它能幫助你解決生活中的問題，例如計算價格、距離或比例。最後，你將掌握如何書寫、簡化與理解代數表達式。透過系統化的練習，你會學會整理複雜的式子，將其化繁為簡，並能讀懂式子背後所傳達的邏輯與關係。這不只是技能的累積，更是一種訓練邏輯思維和分析能力的方式。完成這門課程後，你會發現，代數不再是難以接近的抽象概念，而是一種有力工具，能幫助你理解世界、解決問題，並為未來更高階的數學打下堅實基礎。',
        4000, 'Chris Wong', 4, 2, 'ON_SHELF', 4.7, 544, 324, 251,
        'http://localhost:8080/images/algebra.png',
        'https://www.youtube.com/watch?v=Q1Bn2taqRPQ&list=PLSQl0a2vh4HAxgGKXD5Oc_eELflPEddPx', NOW()    , NOW());

-- Course 4
INSERT IGNORE INTO courses (id, name, description, price, instructor_name, user_id, category_id, status, rating_average, student_count, rating_count, total_time, image_url, video_url, created_at, updated_at)
VALUES (4,
        'Economics 經濟學',
        '經濟學其實離我們並不遙遠——每天的選擇、每一次交易，都與經濟原理息息相關。在這門課程《經濟學入門》中，我們將從最核心的概念出發，帶你理解資源稀缺、決策取捨與市場運作。你會先了解到，世界上的資源永遠有限，無論是時間、金錢還是原料，都需要做出取捨。這就是經濟學中「稀缺性」的概念，它影響著每個人的選擇，也塑造了社會的運作規則。接著，我們將探索比較優勢的原理。透過專業分工與貿易，人們可以互補長短，不僅提升效率，也讓每個人更富足。你會學會如何分析誰應該專注於什麼工作，並看到貿易如何創造雙贏局面。最後，我們會引導你使用市場模型來理解供需變化對價格與交易量的影響。你將能用圖表預測市場反應，理解價格波動背後的邏輯，並運用這些知識分析日常生活中的經濟現象——從物價上漲到熱門商品的供需變化。這堂課不只是學理論，而是教你用經濟學的眼光看世界。完成課程後，你將能更清楚地理解決策背後的原因，並掌握改善生活與社會資源配置的基本思維工具。',
        3500, 'Chris Wong', 4, 4, 'ON_SHELF', 4.1, 279, 120, 77,
        'http://localhost:8080/images/economics.png',
        'https://www.youtube.com/watch?v=wCHm5SdNO5U&list=PLSQl0a2vh4HDERCw_ddanXbsDpFWcpL-S', NOW(), NOW());

-- Course 5
INSERT IGNORE INTO courses (id, name, description, price, instructor_name, user_id, category_id, status, rating_average, student_count, rating_count, total_time, image_url, video_url, created_at, updated_at)
VALUES (5,
        '投資入門白皮書',
        '你是否曾想過，當你買下一家公司的股票時，究竟買到了什麼？在這堂課中，我們將帶你理解股票的真正意義，以及為什麼它不僅僅是一張紙或數字。購買股票，其實是購買了公司的一部分所有權。你成為股東的一刻，便擁有對公司收益的分享權，也可能參與部分決策（如投票選董事）。這意味著公司賺錢，你也可能賺錢；公司虧損，你的投資也可能受影響。課程將透過實例解釋如何透過股息、股價增值和市場交易獲利。你還會學到股票市場的運作原理：供需如何影響股價、投資者情緒如何造成波動、以及長期持有和短期交易的不同策略。透過圖表、案例和簡單模擬，你會理解股市背後的邏輯，而不只是隨波逐流。此外，課程也會介紹風險管理的重要性。股票投資既有機會，也有挑戰。了解你所購買的股票代表什麼，能幫助你做出更明智的投資決策，並將金融概念與現實生活緊密連結。完成這堂課後，你將明白：買股票不是賭博，而是參與企業成長、理解市場運作的一種方式。',
        2000, 'Chris Wong', 4, 6, 'ON_SHELF', 4.2, 288, 145, 86,
        'http://localhost:8080/images/investment.png',
        'https://www.youtube.com/watch?v=98qfFzqDKR8&list=PLSQl0a2vh4HD1g_teRDEUkwMIIFlB8MJv&index=8', NOW(), NOW());

-- Course 6
INSERT IGNORE INTO courses (id, name, description, price, instructor_name, user_id, category_id, status, rating_average, student_count, rating_count, total_time, image_url, video_url, created_at, updated_at)
VALUES (6,
        'This is 美國歷史故事',
        '這門課程將帶你跨越數百年的美國歷史，從殖民時期的開端一路走到冷戰時期的全球對抗。課程聚焦於戰爭、外交與國際干預三大主題，透過多個具有代表性的事件——包括美國早期殖民地的形成、越南戰爭的爆發與影響，以及美國在韓國、古巴與越南的冷戰介入模式——全面理解美國如何逐步發展並在世界舞台上扮演越來越重要的角色。你將看到歷史事件之間的因果連結：早期殖民社會的建立如何奠定美國的政治文化基礎；冷戰時期的全球戰略如何塑造美國的外交政策；而不同地區的介入行動又如何影響當地社會與國際秩序。這門課程不只回顧時間線，更幫助你建構跨時代的理解框架，讓你以更全面且批判的視角看待美國歷史與現代世界政治。',
        1500, 'Andy Wu', 2, 5, 'OFF_SHELF', 4.0, 97, 177, 101,
        'http://localhost:8080/images/history.png',
        'https://www.youtube.com/watch?v=Qz5zFzvbib4&list=PLAC6B9F15C835224C&index=18', NOW(), NOW());

-- Course 7
INSERT IGNORE INTO courses (id, name, description, price, instructor_name, user_id, category_id, status, rating_average, student_count, rating_count, total_time, image_url, video_url, created_at, updated_at)
VALUES (7,
        '人體百科入門',
        '這門課程以輕鬆易懂的方式，帶你深入了解人體的結構與功能。從血液成分的分離觀察，到骨骼系統如何支撐與保護身體，再到汗液中隱藏的生理訊息，每一個章節都以科學實驗與生活案例為基礎，揭示人體的精密運作。課程透過清楚的講解與示範，幫助你理解血液循環、骨骼功能與體溫調節等核心生理概念。同時，也讓你認識這些生理過程與健康、運動表現、疾病診斷之間的關係。無論你是醫學初學者、科學愛好者，或只是好奇人體如何運作，都能從本課程中獲得紮實而實用的知識。',
        999, 'Kat Lin', 3, 1, 'ON_SHELF', 4.1, 355, 187, 155,
        'http://localhost:8080/images/physiology.png',
        'https://www.youtube.com/watch?v=0-pHx5rKKqI&list=PLSQl0a2vh4HCAXMlb3xNdpGU3UvtcPD93&index=3', NOW(), NOW());


-- 4. Chapters
-- for 基礎物理(一) (id=1)
INSERT IGNORE INTO chapters (id, course_id, title, video_url, duration, summary, descript, created_at, chapter_index)
VALUES (1, 1, '速度與速率',
        'https://www.youtube.com/watch?v=W6Ar0ls6tVA&list=PLSQl0a2vh4HC-daPugvP8kL_zuE3u_3Zr&index=2',
        7,
        '用圖表與數據揭開物體速度與方向的運動秘密。',
        '你是否曾經想過：為什麼有些人走路看起來很快，但實際上卻沒比你早到？或者為什麼同樣的路程，騎腳踏車會比跑步輕鬆？這些看似普通的經驗，其實藏著關於「速度」與「位移」的一整套物理邏輯。在這門課中，我們將從最基本、但也最常被忽略的概念開始：平均速度。你會學會如何用距離和時間，精準描述任何物體的運動。不只是算算式而已，而是親手蒐集數據、製作表格、畫出距離—時間圖表，讓運動的軌跡清楚呈現在眼前。更有趣的是，你會發現圖表並不是老師逼你畫的東西，而是一個能「說話」的工具。它能告訴你物體是快是慢、是否均速前進，甚至透過斜率，你就能一眼認出它的速度。而當你加入方向後，速度便轉變為更完整的概念——速度向量（velocity）。你會開始用新的方式觀察生活：捷運列車加速的瞬間是什麼樣的速度？球沿著斜坡滾下來時，圖表會呈現什麼形狀？操場上跑步時，你的速度曲線又會長什麼樣子？如果你喜歡把日常現象看得更透徹、喜歡用資料證明自己的推論，那這堂課會讓你完全沉浸其中。這是一門教你「看見運動真相」的課程。',
        NOW(), 1);

INSERT IGNORE INTO chapters (id, course_id, title, video_url, duration, summary, descript, created_at, chapter_index)
VALUES (2, 1, '聲波',
        'https://www.youtube.com/watch?v=9u1PjsBfrmg&list=PLSQl0a2vh4HC-daPugvP8kL_zuE3u_3Zr&index=17',
        8,
        '探索聲音在世界中穿梭的科學與奧秘。',
        '你每天都在聽聲音，但你真的了解「聲音」是什麼嗎？當你戴上耳機、聽到雷聲、或用手機語音聊天時，其實你正身處在一場看不見的「擠壓與拉伸」之中——那就是聲波的旅程。在這門課裡，我們將帶你揭開聲音背後的秘密。聲音不是一種神祕力量，而是一種在物質中來回擠壓的壓縮波。你會了解到為什麼聲音不能在太空中傳遞（太空電影裡那些爆炸聲其實都是假的！），也會明白為什麼同樣一句話在水底聽起來完全不一樣。我們會讓你親身體驗聲音的兩個重要特性：振幅（Amplitude） —— 為什麼聲音有大聲、有小聲？靠的就是波動幅度的大小。頻率（Frequency） —— 為什麼低音沉穩、高音刺耳？因為頻率決定音高。你還會探索聲音在不同介質中移動時速度如何改變：在金屬中，聲音比在空氣中快得多；在液體中，它又呈現另一種節奏。最後，你會從「反射」等波動特性中，理解許多現代科技的原理：醫學用的超音波、蝙蝠的回聲定位、船艦的聲納系統……這些不再只是名詞，而會變成你能掌握的知識。這堂課將重新定義你對「聽」的理解，讓聲音從平凡變成驚奇',
        NOW(), 2);

INSERT IGNORE INTO chapters (id, course_id, title, video_url, duration, summary, descript, created_at, chapter_index)
VALUES (3, 1, '動能',
        'https://www.youtube.com/watch?v=FtymZWAOeC0&list=PLSQl0a2vh4HC-daPugvP8kL_zuE3u_3Zr&index=12',
        6,
        '用生活現象揭開動能與運動背後的能量秘密。',
        '你可能從來沒有仔細想過：一顆快速飛來的球，為什麼能痛得讓你立刻掉眼淚？同樣是跑步，為什麼全力衝刺的你比散步的你更容易累？這些看似日常的小事情，其實都指向一個共同的主角——動能。在這門課裡，我們不急著把公式塞進你腦袋，而是從最直覺的感受開始：速度的變化、重量的差異，究竟怎麼讓一個物體「更有力量」？你將親手操作簡單的實驗、觀察數據、讀懂圖表，用自己的方式推理出答案，而不是乖乖背下「質量、速度、動能」這幾個字。課程中，我們會把物理變成一種「探索遊戲」：你會發現質量變大時，動能像直線一樣穩定增加；但速度一旦提升，動能會像突然衝刺的火箭般成倍跳升。原本抽象的概念，會在你的眼前轉變成一個清晰的規律。你會開始用新的角度看生活：滑板為什麼越滑越快？高速公路上的車為什麼危險？運動時改變速度，為什麼感受差那麼多？如果你想理解世界的運作，而不是只是接受別人告訴你的答案——那你會很喜歡這堂課。這是一門把你推向「思考型」科學家的入門課，讓每個動作、每次碰撞都變得有意義。',
        NOW(), 3);


-- for 生活處處有化學 (id=2)
INSERT IGNORE INTO chapters (id, course_id, title, video_url, duration, summary, descript, created_at, chapter_index)
VALUES (4, 2, '元素週期表',
        'https://www.youtube.com/watch?v=Jg3Fo4WD8XE&list=PLSQl0a2vh4HBnhjPgsJU2y1UhMwcYmb5u&index=4',
        7,
        '用週期表讀懂元素的規律與物質世界的秩序。',
        '想像一張地圖，但它不是用來導航城市或國家，而是用來探索「元素世界」。這張地圖，就是你在科學教室裡常看到卻可能沒真正理解過的——元素週期表。在這堂課裡，我們將帶你重新認識這張科學史上最強大的資訊整理工具。週期表不是隨便排出來的表格，它用原子序為排列原則，把所有已知元素井然有序地放在一起。每一格都像是一張元素的身分證，上面包含元素符號、原子序、原子量，甚至暗示著它的行為模式。你會發現，週期表其實充滿了「規律」:第 1 族的金屬為什麼特別活潑？第 18 族的氦、氖、氬又為何如此安靜不反應？為什麼同一族的元素會有相似的性質？更有趣的是，你將看到週期表如何巧妙區分金屬、非金屬與類金屬，揭示它們在化學反應中的角色與特性。這張圖表不只是資訊的集合，它是一扇窗，讓你看到元素之間的關係如何影響物質的結構與行為。學會讀懂週期表，就像掌握了看懂宇宙材料的「語言」。你將能用它預測元素的反應性、物理特性，甚至能從位置推測一個從未接觸過的元素會如何表現。這是一堂把抽象符號轉化為理解世界工具的課，帶你瞭解物質秩序背後的美與邏輯。',
        NOW(), 1);


-- for 你不能錯過的代數課 (id=3)
INSERT IGNORE INTO chapters (id, course_id, title, video_url, duration, summary, descript, created_at, chapter_index)
VALUES (5, 3, '為什麼不能除以0',
        'https://www.youtube.com/watch?v=SQzjzStU1RQ&list=PLSQl0a2vh4HAxgGKXD5Oc_eELflPEddPx&index=28',
        4,
        '探索為何除以零無解，理解數學背後的邏輯與限制。',
        '你可能在學校的數學課上聽過一句話：「除以零是未定義的！」但這句話背後究竟藏著什麼原因呢？在這堂課中，我們將用簡單又直觀的方式揭開這個數學謎團。想像你有一些蘋果，要分給朋友。如果你有 10 顆蘋果，要分給 2 個朋友，每個人就得到 5 顆；分給 5 個朋友，每人得到 2 顆。那麼，如果朋友數量是 0，應該每個人分到多少呢？這個問題就無法回答——因為沒有朋友可以分，你無法計算「每個人得到多少」，這就是除以零為何無法定義的直覺例子。課程將進一步解釋從數學的角度：除法其實是尋找一個數，使得乘回除數後能得到原數。若除數為零，無論選什麼數乘以零，結果永遠是零，永遠無法得到原數，因此沒有解。這也避免了數學中出現矛盾或不合理的結果。透過圖形、數值例子與生活應用，你會發現「除以零未定義」不是單純規則，而是數學邏輯與現實世界一致性的保護機制。理解這個概念後，你將能更靈活地應用數學法則，也能避免初學者常犯的錯誤。',
        NOW(), 1);


-- for Economics經濟學 (id=4)
INSERT IGNORE INTO chapters (id, course_id, title, video_url, duration, summary, descript, created_at, chapter_index)
VALUES (6, 4, '生產可能曲線',
        'https://www.youtube.com/watch?v=_7VHfuWV-Qg&list=PLSQl0a2vh4HDERCw_ddanXbsDpFWcpL-S&index=9',
        4,
        '理解取捨、效率與「其他條件不變」原則的經濟思維。',
        '你是否想過，遠古時代的狩獵採集者每天做出的選擇，其實和現代經濟學息息相關？在這堂課中，我們將帶你從最原始的生活出發，理解經濟決策的核心概念——取捨（Tradeoffs）。
狩獵採集者面臨的每一個決定，都是資源有限下的選擇：花時間去狩獵鹿，可能就沒時間採集漿果；選擇建造避難所，可能就無法在同一天捕魚。這些簡單又直觀的例子，正是現代經濟學中生產可能性邊界（Production Possibilities Frontier, PPF）的實際應用。PPF幫助我們視覺化資源配置的取捨，理解效率與機會成本。
課程中，你還會學到「其他條件不變」原則（Ceteris Paribus），也就是分析一個因素變化時，暫時假設其他因素保持不變。這個概念是科學分析與經濟模型的基礎，它讓我們能清楚觀察變量之間的因果關係，而不被複雜的現實環境干擾。
透過歷史例子、圖表和模型演練，你將發現，經濟決策並非只存在於教科書，而是生活中無處不在的思維工具。從狩獵採集者到現代市場，每一次選擇，都涉及取捨、效率與優先順序。掌握這些概念，你將能更理性地分析資源分配與決策問題。',
        NOW(), 1);


-- for 投資入門白皮書 (id=5)
INSERT IGNORE INTO chapters (id, course_id, title, video_url, duration, summary, descript, created_at, chapter_index)
VALUES (7, 5, '理解盈餘與每股盈餘，連結損益表與資產負債表分析公司價值',
        'https://www.youtube.com/watch?v=RouNXdkC0do&list=PLSQl0a2vh4HD1g_teRDEUkwMIIFlB8MJv&index=16',
        7,
        '理解盈餘與每股盈餘，連結損益表與資產負債表分析公司價值。',
        '你是否曾好奇，公司的盈餘究竟是如何計算的？「每股盈餘（EPS）」又代表什麼意義？在這堂課中，我們將帶你深入了解盈餘的概念，以及它如何連結公司的財務報表——損益表與資產負債表。
課程將從盈餘（Earnings）開始：它代表公司在一定期間內的淨收益，是衡量公司盈利能力的核心指標。你將學會如何從損益表中找出營收、成本與淨利，理解每個數字如何影響公司的盈餘。
接著，我們會介紹每股盈餘（EPS）的計算方式：把淨收益除以流通在外的普通股數量，得到每一股代表的盈利價值。EPS不僅是投資者評估公司價值的重要指標，也影響股價與投資決策。
最後，課程將說明盈餘與資產負債表的關聯。盈餘增加會影響股東權益，反映公司累積的財富；而盈餘不足可能帶來財務壓力。你將學會如何把這些財務報表連結起來，全面理解公司的財務健康狀況。
完成這堂課後，你將能像分析師一樣看懂財報，理解盈餘、EPS如何影響公司價值，並用這些知識做出理性的投資判斷。',
        NOW(), 1);


-- for This is 美國歷史故事 (id=6)
INSERT IGNORE INTO chapters (id, course_id, title, video_url, duration, summary, descript, created_at, chapter_index)
VALUES (8, 6, '越戰',
        'https://www.youtube.com/watch?v=9e9GWdT2pEQ&list=PLAC6B9F15C835224C&index=9',
        7,
        '全面解析越戰歷程及其政治、社會與歷史影響。',
        '越南戰爭是二十世紀冷戰期間最具影響力與爭議的衝突之一，它不僅改變了東南亞的地緣政治，也深刻影響了美國與全球社會。在這堂課中，我們將帶你全面了解這場戰爭的起因、過程與後果。
課程將從歷史背景開始：法國殖民統治結束後，越南南北分裂，冷戰對抗使美國捲入戰爭，試圖阻止共產勢力擴張。你將學習戰爭策略、主要戰役與重要領袖的角色，並分析南北越人民的生活如何被戰爭改寫。
此外，課程將討論越戰對國際與國內的影響：媒體報導如何改變美國民眾的觀感、反戰運動如何影響政策，以及戰爭對退伍軍人和越南平民的長期影響。你還會了解戰爭結束後的政治重組、社會復甦與歷史教訓。
這堂課不只是事件回顧，更幫助你理解戰爭背後的政治、社會與文化因素。通過分析不同觀點與資料，你將能以更全面的角度看待這段歷史，理解越南戰爭如何塑造現代世界秩序。',
        NOW(), 1);


-- for 人體百科入門 (id=7)
INSERT IGNORE INTO chapters (id, course_id, title, video_url, duration, summary, descript, created_at, chapter_index)
VALUES (9, 7, '人體骨骼結構與功能探索',
        'https://www.youtube.com/watch?v=-lrKDRAbP38&list=PLSQl0a2vh4HCAXMlb3xNdpGU3UvtcPD93&index=2',
        7,
        '探索骨骼結構與功能，理解身體支撐、運動與保護機制。',
        '你可曾想過，人體如何在日常活動中保持支撐、靈活與保護內部器官？答案就在我們的骨骼系統中。在這堂課中，我們將帶你探索骨骼的結構與功能，了解它如何支撐生命的每一個動作。
課程將從骨骼的基本結構開始：了解不同類型的骨頭（長骨、短骨、扁骨、 irregular bones）及其組成，從骨質、骨髓到關節，揭示骨骼如何與肌肉協同運作，使身體能夠行走、跳躍與搬運物體。
你還會學到骨骼的多重功能：
支撐與形狀維持：骨骼是身體的框架，支撐軀幹和四肢。
保護內部器官：如顱骨保護大腦、肋骨保護心肺。
運動與杠桿作用：關節與肌肉合作，使肢體能靈活運動。血細胞生成與礦物質儲存：骨髓產生血細胞，骨骼儲存鈣與磷等礦物質。
透過圖解、模型和案例，你將不僅了解骨骼的形態，更能理解它在身體健康、運動與疾病防護中的關鍵作用。這堂課將幫助你全面掌握骨骼系統的結構與功能，並欣賞人體設計的巧妙。',
        NOW(), 1);

--orders
INSERT IGNORE INTO orders (id, user_id, order_status, merchant_trade_no, total_amount, discount_amount, final_amount, created_at, updated_at) 
VALUES (1, 1, 'PENDING', 'OCP20231223233917001', 3500, 0, 3500, NOW(), NOW());

--order_items
INSERT IGNORE INTO order_items (id, order_id, course_id, price_at_purchase, quantity, created_at, updated_at) 
VALUES (1, 1, 4, 3500, 1, NOW(), NOW());

--reviews
INSERT IGNORE INTO reviews (id, user_id, course_id, rating, comment, created_at) 
VALUES (1, 1, 1, 4, '讚', NOW());

--coupons
INSERT IGNORE INTO coupons (id, user_id, code, discount_percent, created_at, expiry_date, is_used) 
VALUES (1, 1, 'HELLO2025', 10, NOW(), NOW() + INTERVAL 15 DAY, 0);

--enrollments
INSERT IGNORE INTO enrollments (id, user_id, course_id, enrolled_at) 
VALUES (1, 1, 1, NOW());

-- Sync course total_chapters with actual chapter count
UPDATE courses c 
SET total_chapters = (SELECT COUNT(*) FROM chapters ch WHERE ch.course_id = c.id);
