PGDMP     6    ;                {           test #   14.7 (Ubuntu 14.7-0ubuntu0.22.04.1)    15.1 9    .           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            /           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            0           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            1           1262    16385    test    DATABASE     l   CREATE DATABASE test WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'C.UTF-8';
    DROP DATABASE test;
                lukassql    false                        2615    2200    public    SCHEMA     2   -- *not* creating schema, since initdb creates it
 2   -- *not* dropping schema, since initdb creates it
                lukassql    false            2           0    0    SCHEMA public    ACL     Q   REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;
                   lukassql    false    5            �            1255    16519    add_comment_notify()    FUNCTION     �   CREATE FUNCTION public.add_comment_notify() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

BEGIN
PERFORM pg_notify('new_comment', NEW.ID::text);
RETURN NEW;
END;
$$;
 +   DROP FUNCTION public.add_comment_notify();
       public          postgres    false    5            �            1255    16462    add_message_notify()    FUNCTION     �   CREATE FUNCTION public.add_message_notify() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

BEGIN
PERFORM pg_notify('new_message', NEW.ID::text);
RETURN NEW;
END;
$$;
 +   DROP FUNCTION public.add_message_notify();
       public          postgres    false    5            �            1255    16465    remove_reactions_notify()    FUNCTION     �   CREATE FUNCTION public.remove_reactions_notify() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

BEGIN
PERFORM pg_notify('update_reactions', OLD.message_id::text);
RETURN NEW;
END;
$$;
 0   DROP FUNCTION public.remove_reactions_notify();
       public          lukassql    false    5            �            1255    16460    update_reactions_notify()    FUNCTION     �   CREATE FUNCTION public.update_reactions_notify() RETURNS trigger
    LANGUAGE plpgsql
    AS $$

BEGIN
PERFORM pg_notify('update_reactions', NEW.message_id::text);
RETURN NEW;
END;
$$;
 0   DROP FUNCTION public.update_reactions_notify();
       public          postgres    false    5            �            1259    16389    message    TABLE     �   CREATE TABLE public.message (
    id integer NOT NULL,
    user_id integer,
    content character varying(500),
    time_created timestamp without time zone,
    image_type numeric
);
    DROP TABLE public.message;
       public         heap    lukassql    false    5            �            1259    16499    message_comment    TABLE     �   CREATE TABLE public.message_comment (
    id integer NOT NULL,
    user_id integer NOT NULL,
    message_id integer NOT NULL,
    comment text
);
 #   DROP TABLE public.message_comment;
       public         heap    lukassql    false    5            �            1259    16498    message_comment_id_seq    SEQUENCE     �   CREATE SEQUENCE public.message_comment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 -   DROP SEQUENCE public.message_comment_id_seq;
       public          lukassql    false    5    218            3           0    0    message_comment_id_seq    SEQUENCE OWNED BY     Q   ALTER SEQUENCE public.message_comment_id_seq OWNED BY public.message_comment.id;
          public          lukassql    false    217            �            1259    16394    message_id_seq    SEQUENCE     �   CREATE SEQUENCE public.message_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 %   DROP SEQUENCE public.message_id_seq;
       public          lukassql    false    209    5            4           0    0    message_id_seq    SEQUENCE OWNED BY     A   ALTER SEQUENCE public.message_id_seq OWNED BY public.message.id;
          public          lukassql    false    210            �            1259    16395    message_reaction    TABLE     �   CREATE TABLE public.message_reaction (
    id integer NOT NULL,
    type numeric,
    user_id integer,
    "time" timestamp without time zone,
    message_id integer
);
 $   DROP TABLE public.message_reaction;
       public         heap    lukassql    false    5            �            1259    16400    message_reaction_id_seq    SEQUENCE     �   CREATE SEQUENCE public.message_reaction_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE public.message_reaction_id_seq;
       public          lukassql    false    211    5            5           0    0    message_reaction_id_seq    SEQUENCE OWNED BY     S   ALTER SEQUENCE public.message_reaction_id_seq OWNED BY public.message_reaction.id;
          public          lukassql    false    212            �            1259    16401    message_received    TABLE     o   CREATE TABLE public.message_received (
    id integer NOT NULL,
    user_id integer,
    message_id integer
);
 $   DROP TABLE public.message_received;
       public         heap    lukassql    false    5            �            1259    16404    message_received_id_seq    SEQUENCE     �   CREATE SEQUENCE public.message_received_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE public.message_received_id_seq;
       public          lukassql    false    5    213            6           0    0    message_received_id_seq    SEQUENCE OWNED BY     S   ALTER SEQUENCE public.message_received_id_seq OWNED BY public.message_received.id;
          public          lukassql    false    214            �            1259    16405    user    TABLE     �   CREATE TABLE public."user" (
    id integer NOT NULL,
    username text NOT NULL,
    passwd text,
    email text,
    bt_mac text,
    token text,
    token_exp_date date,
    token_exp_time time without time zone
);
    DROP TABLE public."user";
       public         heap    lukassql    false    5            �            1259    16410    user_id_seq    SEQUENCE     �   CREATE SEQUENCE public.user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 "   DROP SEQUENCE public.user_id_seq;
       public          lukassql    false    5    215            7           0    0    user_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.user_id_seq OWNED BY public."user".id;
          public          lukassql    false    216            {           2604    16411 
   message id    DEFAULT     h   ALTER TABLE ONLY public.message ALTER COLUMN id SET DEFAULT nextval('public.message_id_seq'::regclass);
 9   ALTER TABLE public.message ALTER COLUMN id DROP DEFAULT;
       public          lukassql    false    210    209                       2604    16502    message_comment id    DEFAULT     x   ALTER TABLE ONLY public.message_comment ALTER COLUMN id SET DEFAULT nextval('public.message_comment_id_seq'::regclass);
 A   ALTER TABLE public.message_comment ALTER COLUMN id DROP DEFAULT;
       public          lukassql    false    217    218    218            |           2604    16412    message_reaction id    DEFAULT     z   ALTER TABLE ONLY public.message_reaction ALTER COLUMN id SET DEFAULT nextval('public.message_reaction_id_seq'::regclass);
 B   ALTER TABLE public.message_reaction ALTER COLUMN id DROP DEFAULT;
       public          lukassql    false    212    211            }           2604    16413    message_received id    DEFAULT     z   ALTER TABLE ONLY public.message_received ALTER COLUMN id SET DEFAULT nextval('public.message_received_id_seq'::regclass);
 B   ALTER TABLE public.message_received ALTER COLUMN id DROP DEFAULT;
       public          lukassql    false    214    213            ~           2604    16414    user id    DEFAULT     d   ALTER TABLE ONLY public."user" ALTER COLUMN id SET DEFAULT nextval('public.user_id_seq'::regclass);
 8   ALTER TABLE public."user" ALTER COLUMN id DROP DEFAULT;
       public          lukassql    false    216    215            "          0    16389    message 
   TABLE DATA           Q   COPY public.message (id, user_id, content, time_created, image_type) FROM stdin;
    public          lukassql    false    209   �C       +          0    16499    message_comment 
   TABLE DATA           K   COPY public.message_comment (id, user_id, message_id, comment) FROM stdin;
    public          lukassql    false    218   ,P       $          0    16395    message_reaction 
   TABLE DATA           Q   COPY public.message_reaction (id, type, user_id, "time", message_id) FROM stdin;
    public          lukassql    false    211   �T       &          0    16401    message_received 
   TABLE DATA           C   COPY public.message_received (id, user_id, message_id) FROM stdin;
    public          lukassql    false    213   �Y       (          0    16405    user 
   TABLE DATA           l   COPY public."user" (id, username, passwd, email, bt_mac, token, token_exp_date, token_exp_time) FROM stdin;
    public          lukassql    false    215    ]       8           0    0    message_comment_id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public.message_comment_id_seq', 158, true);
          public          lukassql    false    217            9           0    0    message_id_seq    SEQUENCE SET     ?   SELECT pg_catalog.setval('public.message_id_seq', 1083, true);
          public          lukassql    false    210            :           0    0    message_reaction_id_seq    SEQUENCE SET     H   SELECT pg_catalog.setval('public.message_reaction_id_seq', 1149, true);
          public          lukassql    false    212            ;           0    0    message_received_id_seq    SEQUENCE SET     G   SELECT pg_catalog.setval('public.message_received_id_seq', 977, true);
          public          lukassql    false    214            <           0    0    user_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.user_id_seq', 157, true);
          public          lukassql    false    216            �           2606    16506 $   message_comment message_comment_pkey 
   CONSTRAINT     b   ALTER TABLE ONLY public.message_comment
    ADD CONSTRAINT message_comment_pkey PRIMARY KEY (id);
 N   ALTER TABLE ONLY public.message_comment DROP CONSTRAINT message_comment_pkey;
       public            lukassql    false    218            �           2606    16416    message message_pkey 
   CONSTRAINT     R   ALTER TABLE ONLY public.message
    ADD CONSTRAINT message_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.message DROP CONSTRAINT message_pkey;
       public            lukassql    false    209            �           2606    16418 &   message_reaction message_reaction_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY public.message_reaction
    ADD CONSTRAINT message_reaction_pkey PRIMARY KEY (id);
 P   ALTER TABLE ONLY public.message_reaction DROP CONSTRAINT message_reaction_pkey;
       public            lukassql    false    211            �           2606    16420 &   message_received message_received_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY public.message_received
    ADD CONSTRAINT message_received_pkey PRIMARY KEY (id);
 P   ALTER TABLE ONLY public.message_received DROP CONSTRAINT message_received_pkey;
       public            lukassql    false    213            �           2606    16422    user unique_username 
   CONSTRAINT     h   ALTER TABLE ONLY public."user"
    ADD CONSTRAINT unique_username UNIQUE (username) INCLUDE (username);
 @   ALTER TABLE ONLY public."user" DROP CONSTRAINT unique_username;
       public            lukassql    false    215            �           2606    16424    user user_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public."user" DROP CONSTRAINT user_pkey;
       public            lukassql    false    215            �           2620    16520 )   message_comment add_comment_event_trigger    TRIGGER     �   CREATE TRIGGER add_comment_event_trigger AFTER INSERT ON public.message_comment FOR EACH ROW EXECUTE FUNCTION public.add_comment_notify();
 B   DROP TRIGGER add_comment_event_trigger ON public.message_comment;
       public          lukassql    false    218    222            �           2620    16463 !   message add_message_event_trigger    TRIGGER     �   CREATE TRIGGER add_message_event_trigger AFTER INSERT ON public.message FOR EACH ROW EXECUTE FUNCTION public.add_message_notify();
 :   DROP TRIGGER add_message_event_trigger ON public.message;
       public          lukassql    false    219    209            �           2620    16464 /   message_reaction remove_reactions_event_trigger    TRIGGER     �   CREATE TRIGGER remove_reactions_event_trigger AFTER DELETE ON public.message_reaction FOR EACH ROW EXECUTE FUNCTION public.remove_reactions_notify();
 H   DROP TRIGGER remove_reactions_event_trigger ON public.message_reaction;
       public          lukassql    false    221    211            �           2620    16461 /   message_reaction update_reactions_event_trigger    TRIGGER     �   CREATE TRIGGER update_reactions_event_trigger AFTER INSERT ON public.message_reaction FOR EACH ROW EXECUTE FUNCTION public.update_reactions_notify();
 H   DROP TRIGGER update_reactions_event_trigger ON public.message_reaction;
       public          lukassql    false    220    211            �           2606    16425    message_reaction fk_message    FK CONSTRAINT     �   ALTER TABLE ONLY public.message_reaction
    ADD CONSTRAINT fk_message FOREIGN KEY (message_id) REFERENCES public.message(id) NOT VALID;
 E   ALTER TABLE ONLY public.message_reaction DROP CONSTRAINT fk_message;
       public          lukassql    false    3201    211    209            �           2606    16430    message_received fk_message    FK CONSTRAINT        ALTER TABLE ONLY public.message_received
    ADD CONSTRAINT fk_message FOREIGN KEY (message_id) REFERENCES public.message(id);
 E   ALTER TABLE ONLY public.message_received DROP CONSTRAINT fk_message;
       public          lukassql    false    3201    213    209            �           2606    16512    message_comment fk_message    FK CONSTRAINT     ~   ALTER TABLE ONLY public.message_comment
    ADD CONSTRAINT fk_message FOREIGN KEY (message_id) REFERENCES public.message(id);
 D   ALTER TABLE ONLY public.message_comment DROP CONSTRAINT fk_message;
       public          lukassql    false    218    209    3201            �           2606    16435    message_reaction fk_user    FK CONSTRAINT     �   ALTER TABLE ONLY public.message_reaction
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public."user"(id) NOT VALID;
 B   ALTER TABLE ONLY public.message_reaction DROP CONSTRAINT fk_user;
       public          lukassql    false    215    3209    211            �           2606    16440    message_received fk_user    FK CONSTRAINT     x   ALTER TABLE ONLY public.message_received
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public."user"(id);
 B   ALTER TABLE ONLY public.message_received DROP CONSTRAINT fk_user;
       public          lukassql    false    215    3209    213            �           2606    16507    message_comment fk_user    FK CONSTRAINT     w   ALTER TABLE ONLY public.message_comment
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public."user"(id);
 A   ALTER TABLE ONLY public.message_comment DROP CONSTRAINT fk_user;
       public          lukassql    false    3209    215    218            �           2606    16445    message message_user_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.message
    ADD CONSTRAINT message_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(id) NOT VALID;
 F   ALTER TABLE ONLY public.message DROP CONSTRAINT message_user_id_fkey;
       public          lukassql    false    215    209    3209            "   q  x��Yˎ�]����K��]�w�dE�am$+B`@ ���LSRJ+o��� Y�I c���}�b4?�/�'��[�Ѱ��� 2�,x|�V�}�{n1I}�ɣ��t%~�:?��h��G�~���a`�@�*%%�:�w�4��i3^��8k����V���bu�?}�ǿ����Ksi�m�>�|��,�{6��ߘ+�89p�

��'
+<����� �
�]�6�00�Qǳ�ju*&���(V�r.U*���ē(O>����������_>���*���<�MȫR��ځԕ5!D�@%��_�֛��<y�QUr�j��R'Q�<k֛������d�ϖ5!�mݖHM������IK�iy �M��Q���_���xTb�@�*�U�XGXar�e�R)�Yy�~2[-
�1��B��=XO����,��SA�((�����P�!V�)r+rXq�����댌��Z��׻I	tt!�I��Z����g2 /q�(�[���@MJ�Hr*3��3d���颼�C�u�[K�mmy��nV���@�J{��c�����q�URK��]��m�#@�+m��9�:��dԻ7�(F�A �h��@�-Ƌ>�|�&�(�����Iٛҡ�<!h|1��^��Q�y�p��&��V�J�rv�㶏T��	u��&�~����X�V*tF9LzlC�Q�&�e8RO��lV@�@"S�YJy�=A9Z�G��hׇZ0�U�d���#HM�+�c`���ޚ��5ø����{PC{K�Ld*�L}��2�@��R���,�qY��t.5)��Vw_�<罍�1��%��>A��S�ɢͧ���o�V�Q��>�s���O�J����m���ag�)oCve���R�rA�|�����Ψ�'-:�3!���y��6���r��)8���h6Fi����/;br��F�Y����AH�298�۶gӠ�U� 58���^?-5'QDe�܎�G���}e��(:$��^�t���k���><۞E��TQ�~32�G<��D���呋�*}�{�A�)4!>b��{��!=7��B�x��rg)�l
���#s�ttVV����76(N6ρ���c��B�s��3�������<_,�H�1P2�<�Q�́��^��90�f%:"Uֻ��>�!�ZY� #�1y2�Y���ʀ��[�!��4�Po2A��@��c�Q���'��#�Cw^���ۻ�LTU�r����(�b����:��ϷǐgH7��_�A��JzEU��0(��j��葲ܔ����W�����!�K�� #	K$M�|1]����eq�y��D6��zW1��
iH�#��ܸ�m�B�
7٦νp=]�H�N�t3���ZJcuBrX~+6��J��?lW�k�h��b3\�v(F���� �����x�߯�����wF�VT�\��3�a���PҐ3�FsRD��}���I��c]��5�sh4�o��LQ�g��z�F�yN��������2$f�G��C��I��`d�[&��S�v=Z/v}(����UR��`{�ގJ�c�IB�w>�\�I�(E#��Z%q��k�>�Ϣ���|!����1�3�-4N��J��ـ~},�4)���(=�D��@��}��s(ǈ��zX`�t��,G	c$��d6��K�����|ޔ�8OLnB�hEC�����O�������}`.; �l.O 0g;�[�Ϙ����|ޮ6��/)>�?����e|Yڵ���2�^�ut��f>]��� ʚ��@�cbb�dJJ��K�E�%[���7yA����"S�S;�y��ѧ<�'M'U�6���s�5������cF��7��c���[hS�dt��N��ň��g"�A:�Fɜ-��J��pGL�1O� ����?��f4䌗�wNW:���ɜ��t�A}�΋���ǟ>��"��������?�-��2.Zq�cCgh�ח�7������fy�����?�O������T)���\���>�HLH��@��lD�-��\|�7���q��V�J�S	a#u"�����۠g6Ri��Q3�\�ף怹i|R�f�`����|����ہ��"�]��N@u�yw��_��Nud��B��[�)�L]2ƬC d|]E���Ϗ���v�H�iL��'%�&�J)���c��Y���g�_C���S>��b�no=6v�H]e���X�����WM�=f���LG�GnG����Uʇ6�^���MF}8&.�$=(�a�A���[�Օ�?,@�F�xL�����~_��� ~�:���::T�)��Hup�m7�vX�=.*�Fg�57u����v��m��~�M{uy��]�_bt�0B�g[��ktΟ��]1훀���^��ʁ\bi`�HخA�� ����N<%�)�����ƙ�>M���Oʆ����)S�u�_���$:��q�|�u}�8/ό��"�D�R�������s19�m�U4�w���M�r��������P:��t�x���vx*6��0��ԫ�i�[���b~v�{��%�ص���p9!{�2�؇�\�箐�f�}oҝNb솜�ܙV�u<���r&>+o�Pkh
�{��rD��O_�X+#Ǿ�@��y{����aV�ao;ܜC�`�oP��{gě��`�ctH������]�n�%���=����� >jJ�A"A��i/��]����˫���y��E��V�p�����o�ԁIfUN+�y��G|��!���эN�;�G�\���k�;��̵���\"��%/�����������R�XK嬽�{M`�U��v����8�/W�jܔ�}��e*�,��}���gDIo��g���X�5�7����7���������%��CT���������rt��H6�+t>w��W�.���+A�"�� QBwM��@��a���ׇ	���"�}*�l�e�����wp��,�#8��o��eL?ﱆh_�w� F��g4��z��PC�V�!�r�O����|����r;$ogZ�c��K8�� ����A�6�}s�e�1]{���gn��� Ks6�:S����Уz�1,��*�3	�m�[�g����؈Q#�NM��'�}�]'N��4��������Y߅N,a� l;���*z��W{g�������&����n�;���	�+w���<���zo��82��"��ͯ�;w��U6=�      +   �  x��V;��4��_����8�E�Ў`v�@,4+�ܼ��%���j;z��bD�a)��?�8�˾3F�������w�6����6�n�3rV!�E��8�E\b�g�b����j���y&ПAf=k�:i|uu��M ��(1�Ԉ:F�5�̈zF��y���'�I�>�mB߼����m�؞k�8;�Q�7w��2�G�}�2����}�t�g/!���/eg��	V��!��8B6��������a�*/m�G歀�t �KQJ��P����C/K�,��+or	S\�JL��@a� N�O�V �]E��1�������Š��r��E
G,nA��-0���l$i�}80l��!b���I�ň1��H���VU^�i�dR6�]y@�o�H�1�E��!C�u�#�@
y�hs8�y��pu��cj&��/���� O#�B�b�B���♤`�W,�B���
#S�S�F�c�J�Շ���_w?�y���7�3IqY�	8�SdGC5˖�}u��鈤�*À|�(�C�r����h��BU�1��l��L�\���������"���o����:?�n_�~ο����I�*_]�zݠ�]d*O���2q�%Z,�������'�	�Mp������&����޿+���XZUB�N�K&�*E��Be��h)�jGQ��$˪�DG�Uy�21\%6- �\�f6Bg"���n>�ᇸ�G��Ur ��
)�e��Ѫw@n}[��¨�l[@�~J�<�cx��ZKu	�Z-������I����# �}d�[���	���\�&Q��$a��{s�E7C��m"�H�U�5��v�/f���:����i�!oB�^~9�	��zT�x.�Q��E_�a��E<�Sx�P-����|R-�ͩ��P-�07 ��$����pU��� �;[O��}VXRm��%j%�����G:���X#����t��S/��1`o��h��=f����cav5vm>�|�0�f�c���$�i7����R!$Qל
xs,{���&�q��]��J����;���y�o��`��x����!އ�uu��6�U�+@��Mr(j	��M��%ʡ7�J��:�0�z�mA��i7�Ф�����d�@��P>��|�L\rSm�5� LA`2��φ
�c�#U�ajS����A���B���� ��b      $   �  x�u�[��
D���L���K��r�?�!Wewe���KH��|e}�T�g�?�g^!��k��WO�#S�4:cz t���yi^2�JW$�$���h�V�A�@�#R��l�%�@Ю���	�,�6	�_�_~�.��(<��g�̡6E_�A��FYNW�쁋9ֲY�[��������C��2�5|u�S+ձ� Ro_K	
����1��ӿڙ�u�Ƭ�ur_搘,�:f#1���!1�[�9��TY��\���-s6@��:�`�?`#.V.��L1��>�uM�L��Z_�����M^&q�¯�:�|F� �"3'�y����oP�ʹ�?��"�N��N��eⳭ	.^M��;� ��f�m���	�z�C����f]�CTs�hi�\�C�e}EA��K|1�$�D�\s[���),�u��M��p�r
���!dؐY.;$�f}^]'�T�S�.(L�,��wr���^(�o2�1�b/�j���d�CLE�4�_/�Hȹ
)�$�*?Ez�%b�?��`�P�Fӛۭ2���`Y-F�n���!Hz�+d?�����q�u��	_�;鬸:��߮�L�7��I�'5g̖��o�T���@�G���G2.�{.u��{���w��1ٖu赾e�"���0�f��4`ё+E~fm�i	�O�����IB�Z����Hߍ~�zFᄰ�2/��Qw����=rr���8U����7����u
�{�;�αo��dsPUc�����@����ărJ���mVH���*��&�>�v�E�,)P�(;�jG�u�]��h�5�w�`�p0���"��A�R̬N��<�1W�MQc��"ŭ���>�%�>�����6�H�3�Ms�ZZ��3(��U� -�^,�`�~��&QTI��o�A��8���=�������HQ���o����¢
�=6�Xg��U�Nuk�X�rJ�`��fr���u%ɒ��@�T�J��&�*;Y����Z�Њb%��dt*� �ދȞˢ;W�������N�����+G\?�X���V�aGr'��lN2��7c����@�V�M�PL{O��$:��*ŸSy�4Qln��F�����d��T*Y��]t@�V����Q|�3��u�:��m�Mm���k^P�?7�/�cL�:��dR��+ٽ;�7�����%�ݓO~��|�>�@�]P;�-�	���2�h>E���(۩�Pa<������ۮ�OD��V�j�{�_��Ĳ��Tn����:����0�jX��h?E�&��gK���7������,      &   �  x�M�K�� �U���W�����1�SX�l!��d�����u}ת�]�6���)h��)h�ĕ�]?kߵK�l����v��CHg{�gt�-q	ir�;�m~�{bs�:6��c�([��f�����Ӄk���5[G�~q�Fe�m4P��A��e�6(���N�kI��-�'�ę����[e��[w���d�L�d�L�d��m2P�m2P�m2P�lqg�d3zh��.�a��.�a��.����.���-]�u�-C��bȶ�bȶ�a�D�l6�m6����l�l&ۦ�!�f�)ۦ�)ۦ�)ۦ�)��6G�L�ĕ��E+�5�%���fi��Y�L6�L���9D�8�p�� �Y���#q&Z�J܉��.��C��%v!���ݜS�,y�T� K^܉.䳐%/�l����+���g��ӕ��[Ka��Z-��U�k�[p��O����a��/�\�W������N��f�5�~jxIy�=�~����ވ��9A�-L5�{<��v��0����y�~j���Zx��]�a�}N�
�7"E��c�ý�=�������|��"Q��Z�yJ<K�4Frd��I�6����"��_/�/�`���"�v�,��1��|�����!�Y̳@N�hA��QF�Կo�]���.�����#!����])�NV�O�ԍv�ڭ�5�����΂��6@����r����Oxv^q�Oѝَeݟ3�S��B�	-�i���,��������)|f      (      x���ْ����]ϱoE�d���V�E��n��A��{��bW��Z�w���X��9����$@�m�l�/���m�XWֶ6��5��}^�P\�W�-[o�hvƴ�hw�BHBw'����;��$��2�`�doT���RRv��mT�i��Y.� �@SM!� �'~ �h��i��N���>��K��28���O5�y؆m�K]��!oΊe/�aMw+�c��쀛\���'PE�C�4���]5~!�B
mD� �7>��D�L��8I�85���+CH?���+F'�t�t��S�j6jȯX�f{�G$�[q<̲�t6q5�I{	�O,;����.#|wEK�;_
�x��w?���?`�bp@N���^�H(�Z�҇h��<y��i����A'��,��R"b[��P���$J
7����0xG�p��1�
	�'��`q�B�XΆ����A�SC�M�14�)�	�G1%_��cܳ���Y�W,�a�@ܯٓΆ-ϫ%�̔�v�\��C������r����KvpjN&���j~7gJ�����J8��(��8��'����q���cp޺�Y!����5�Ί��9mv��&����ޒ��c���H.-(�>6�2�綊�S��r8~�m�B�' � �� ,ɢ1�Ԅ;�~��O��fϝҢ�I��Z`v�5��Aw��eM�Ɖ��<7X�c�n�36�Om������R�p��L�r�h�Uw|Kxc�!E��~�1��p����S?l& �|���tq�:��H4�u���C��lH"���Oy�^8e���C$�=/b�O�؊:-6����ph����MX�+�R|�� ��-Q�;0�!������;�Ȍ��VB������w��mZ~��9TAc�aH��Š?�A�y�z�$��u�pJ���<�Z6��Y�m�J.AW<�a�}*�wK~��vn���ҽd}��'o�>O�5��b�n�*j���u����{U@#FG1�n�<k7�]�̔ ��uQ�k�yƿm{�J	�L�U���\��$�%�J9eݤ�ɛ�O��d�`�}�� ߩ{�GkΌ�{�r�]�$0�����4�}�O�˗s?IֶUf��kH�l�b��Y�=V-B(,<$�����l6��e�9y3�IIBU�Uﾮ躧��<Z|g:�;À�%	1�D�]��	�A�Q���+9K�x���jQ���R�6��3Gr�N����W.}�*�d�j5y3�	y�$������ ��hgk%�.�s�A��M2 !��/�{X�e�>�du���Q���2XU<����8�N�Q_� ���{�mA�b*�?L��Y�G����g\ߣ=b�y��R�#V��!�T�����h'��ۜ��u7�ɷ�}�t����M�}
KJ���уfm���!�j�(T}�P�ħh�f�/��]�O.�b-)V����T�U��!Ӵ�H8VG�]�|�Άm�VyX����xg�r-4/@q]�U,��w�� *À����h0�A�8�.�����X �iH<�e��Bg�0UWz�itY�9�I�z�ҮQ�Uv��֠�~�f���_�q�I$���nCK;����Q|��Vv�D�S��_�h��lh5�2�;�����va;� c��;%*��>p\<�$�����RE�b�ɼ�������yƂsmpw�6��E��VҰB�[)��:D�jy����֥��vJ�s=hr���kI񟎣KY����Γ��z)(�2_T�q�ݲM/�����m��3,nՀ��Tq�����U+��C���h��AW7�ꞎ��G��Q����X�����|ʾ��&`sIw���*87��[Rym����D�Rԭ�M�����E�H{�+��,>��l����M��w�2@!D=����A��?��G�s�UƖ�+ʰ�Im���WM��G�Mnʱ7Z�#����\�~.��"tq�h��+e�lo�h���;]� 0�D�����.��e����4jO���Io)Î��T
'����F�b����f�u+��ؑ�2�	��WmomZ��J i�8i�yi.�oH���~S��_�a�W����<���:��kx�O�N������\;���[��fQ���lh'���!���y��e&��($����X��r�+�� l���/@<{\��{�>F���u�R�k���k{����f�1�}��Zl��Ut��O�}R�a�$^��}D��Z��j����Z�����FJb4)��Ų�T�O.���)i� $"K�q�<VX�%r�e�7�c}?z�`S�XcR����DOF4=�s�l}�����J���J�_�
�Dظh��)�)��A䢩�>��w��N7�����r�	�9�����NA#���r���"�$�7C�^w��+(x�ݮR�M⒅��'~�ꇠ0g	��ŀ�F���4d_��S�lV+p.�zz��A�-�*�ܱ�'r�z3^����,hN��FE��C�r��p��8��	�z�5�_�C���R�m3�]�c,�Q�§*���N��	��xO�f�K�&4�x�a^�%8�}�Tl�C��N�	9�$���&o�>]��6����c����ܻ��n�o���Pcy����O~�O���i�%lf�?�Z����rnG��@D�h��\�F���� X����RW�S]Q!Ŏ+P8�^5�\���l�De��<��dm��p�I�	-G؆�Z�X�Z#��}�f�k� k�pU~��XQ�X����Q�M�S��$|�ot1R>�j�Tʟ�`-<�~x�]{�����&(��N��W>�n�]��=�+�q'o�>)=j}\�	h�^�:��!%�s{�����)G5CG�| `���]j�Œ�i��M�TJ9�����p]K�
z8�ypi������N��}���5�*�H�I�Z�Y�K7���.n�����; ~���c-�h�������D�{D����Wk{i2�VK�$+��ݳ�B}l���Ш0d�`��܄? �i|�E�z;F�.b΄�Z�z2vGi1��>(�}6�{ܔQ�����ExË���]����ѝ�AQ�X�*i�X4��&�%����uF���8A˷F}�b�.+
I0>�|ɻ���#Ƣ�0��)H��{^Fņ�N��k=8���ᑻט%��5�kv���z\ԃL��f�~^;�﷓7C���~{>�fr"��nv�Zٺx�3�[��ۋ���a���ާ2a@�!�� xRb�Q�n�:;�6��|�f��P �tu�ouѲ|�v�L�������t]�� �׫��Wq���N@*��M�{�fH�]��?gī�,,b��,Gf����n�.4*�ߣ�#����T��h[�߫������,�a�!>��h�.�ࡷ���x�z|��B��ʀ�kaN�3f7T+�r[]��j�1Oײ�����<s���\��m�e,�I�������Q�3�C�	N�IO<��<�=86h���!5q�
�|!�P��!1��2��xl��ii��TG��$'�9�ɛ�O�dj1��-��D�\�;~m�B6�]˾���8�C�4A?. ��L2�E���΃l�7��bb�֖qga�U��;� s�H{���e�������r=���KZKI���})�fw��s_���C�$y�2w#b�D�7�+� 8y3�yh�/ ��zV���)%����f�������,M�^��8A!AM���zu5�B�;�T�euV�����W��s�+w_�vC�sG/eo{n�f�Ke/�C��
��6B����q,7��$$�?d ���Gω�1�'�|�H�i����S�c?�a�l��Vٜ�5 ���C���m-��!�����·念�tH\�A-k��m{ �E��l,`c-Q9R����k��:q�Z'P1fY�R�1:S�N��2ח|8r'���D��so�t���������������]\*\�e9�����-Z{ny�����0��|L�ǽ�8|^���6��׻��
,����|��J�Ơ��$>�<�HyL��[� 	  ?q�a�[h�N�NqT�mC��D�7��~���X���僱@!AL�W��6qY'>宗A�=-Ǽ���ƻ����
wk�J�f��.|>y3�I����L�s�~jَ�,(�zc�"_,��r\�3�O�aG��^Ց0�(ʙW��L~�`�H��eݴ}�jY��L��j'�� 4>9�	��s�ŕ��L�}�י��/���m����ܯk0k��)�j�1D!��()Ĳ xS��خ�5{&�Q�	?\�,�f����}!m����H��������	g��7C��r�!}��{B�m���W�m ?��SB�ѸV=(�� ����kqqӹFK��iž��d���37.�*oeU�Τ�Va��D�Y0��7C_s�._'PJ�B�21�`�j��d�o�A�N=j�9ti�x�����]�8�;3)�i��FV07m��(�{`��ZHdn�ϭ�{�G�^��7C����&���B_���-)1D�@do��\S>j�!d	4R>���{�zh
^�:��H���C�C��M�\�vz��u�׍�R��:�;�2Y�M�}R�EJ߬�G�9����h�$-�L�EIJ4J�G^�`rq�v��Q�2���g�ŉ���U�E�M��\�
U��ڃ���#6vl=װ*o�w�9�� h�js�i1�07�Q�%r�hh�Ɏ��|8�|�:b�ũ)z�Yi~��N;?(p$	��(���v+< ��q��bЏ��o��&o�>)�U�L���\k�����b��~��7���7=VF��
�R�|6�G��^�i�j�[�����UUg6�Ņ��K�Og���Y�\��3V���Q�~�f�dd���p�Ȅ隡栃Wq�o�wJ�� ;���/�Mң"<p�`�׸������4n��\l�ìq��1�M6C@���!f��(8z����'���D�-��A���4�=&e ��̬oJ��`ƹ���� ��=�
��KM[�2L�Vs�d'��P(�j'��Dg�'V�;����I%��������'e�S�ީ��S�&
PD��ݮ�����7r�m!A�����+���}�����h	1��a��M�.��r�^�x��]��]��D=�2cPS�k+��EUR��_�ge�+� ����{2n�w�k��H2��)j�(|*`��k+#9J�����X鲿�wC��Q��昻3���ݹ��W�Y�w��j�ɛ���ь�)��V]�ۣ?~E5\�9I����E����<��QՒ��k�}��FS�\�QV;g�ze�0ѴAr�(��x����(��%��ee!Q�7C���ݷQ�+��;���&�/��d����Bhg�QՒ�=�p�����^�L�^^��a�Pk��vRK
����6�� wwQZ-�[ry����,�y{]�9����yQ��z��̛�C� �	�<6�uWK�;����׼B���[׾-5��m�mhG(׎��M��lB��8F?�]v�'o�>)�iP����S�S@��)9[�Q���!/�!�h�~���ׯ�U��U     