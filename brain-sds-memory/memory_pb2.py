# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: memory.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='memory.proto',
  package='',
  syntax='proto3',
  serialized_options=_b('\n\025maum.brain.sds.memory'),
  serialized_pb=_b('\n\x0cmemory.proto\"-\n\nMemMessage\x12\x0e\n\x06status\x18\x01 \x01(\x05\x12\x0f\n\x07message\x18\x02 \x01(\t\",\n\x0bMemUserInfo\x12\x0c\n\x04host\x18\x01 \x01(\t\x12\x0f\n\x07session\x18\x02 \x01(\t\"?\n\tMemIntent\x12\x0e\n\x06intent\x18\x01 \x01(\t\x12\x10\n\x08hierachy\x18\x02 \x01(\x05\x12\x10\n\x08\x65ntities\x18\x03 \x01(\x08\"6\n\tMemEntity\x12\x13\n\x0b\x65ntity_name\x18\x01 \x01(\t\x12\x14\n\x0c\x65ntity_value\x18\x02 \x01(\t\"\xde\x01\n\x07MemTurn\x12\r\n\x05utter\x18\x01 \x01(\t\x12\x1a\n\x06intent\x18\x02 \x01(\x0b\x32\n.MemIntent\x12\x1c\n\x08\x65ntities\x18\x03 \x03(\x0b\x32\n.MemEntity\x12\x10\n\x08lifespan\x18\x04 \x01(\x05\x12\x1a\n\x04user\x18\x05 \x01(\x0b\x32\x0c.MemUserInfo\x12*\n\tentitySet\x18\x06 \x03(\x0b\x32\x17.MemTurn.EntitySetEntry\x1a\x30\n\x0e\x45ntitySetEntry\x12\x0b\n\x03key\x18\x01 \x01(\t\x12\r\n\x05value\x18\x02 \x01(\t:\x02\x38\x01\x32\xfa\x02\n\tSdsMemory\x12%\n\x08UserInit\x12\x0c.MemUserInfo\x1a\x0b.MemMessage\x12$\n\x07UserEnd\x12\x0c.MemUserInfo\x1a\x0b.MemMessage\x12%\n\x08PassTurn\x12\x0c.MemUserInfo\x1a\x0b.MemMessage\x12\"\n\tAddMemory\x12\x08.MemTurn\x1a\x0b.MemMessage\x12\'\n\rGetLastMemory\x12\x0c.MemUserInfo\x1a\x08.MemTurn\x12(\n\x0cGetAllMemory\x12\x0c.MemUserInfo\x1a\x08.MemTurn0\x01\x12+\n\x11GetMemoryByIntent\x12\n.MemIntent\x1a\x08.MemTurn0\x01\x12+\n\x11GetMemoryByEntity\x12\n.MemEntity\x1a\x08.MemTurn0\x01\x12(\n\x0b\x43learMemory\x12\x0c.MemUserInfo\x1a\x0b.MemMessageB\x17\n\x15maum.brain.sds.memoryb\x06proto3')
)




_MEMMESSAGE = _descriptor.Descriptor(
  name='MemMessage',
  full_name='MemMessage',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='status', full_name='MemMessage.status', index=0,
      number=1, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='message', full_name='MemMessage.message', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=16,
  serialized_end=61,
)


_MEMUSERINFO = _descriptor.Descriptor(
  name='MemUserInfo',
  full_name='MemUserInfo',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='host', full_name='MemUserInfo.host', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='session', full_name='MemUserInfo.session', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=63,
  serialized_end=107,
)


_MEMINTENT = _descriptor.Descriptor(
  name='MemIntent',
  full_name='MemIntent',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='intent', full_name='MemIntent.intent', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='hierachy', full_name='MemIntent.hierachy', index=1,
      number=2, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='entities', full_name='MemIntent.entities', index=2,
      number=3, type=8, cpp_type=7, label=1,
      has_default_value=False, default_value=False,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=109,
  serialized_end=172,
)


_MEMENTITY = _descriptor.Descriptor(
  name='MemEntity',
  full_name='MemEntity',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='entity_name', full_name='MemEntity.entity_name', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='entity_value', full_name='MemEntity.entity_value', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=174,
  serialized_end=228,
)


_MEMTURN_ENTITYSETENTRY = _descriptor.Descriptor(
  name='EntitySetEntry',
  full_name='MemTurn.EntitySetEntry',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='key', full_name='MemTurn.EntitySetEntry.key', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='value', full_name='MemTurn.EntitySetEntry.value', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=_b('8\001'),
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=405,
  serialized_end=453,
)

_MEMTURN = _descriptor.Descriptor(
  name='MemTurn',
  full_name='MemTurn',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='utter', full_name='MemTurn.utter', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='intent', full_name='MemTurn.intent', index=1,
      number=2, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='entities', full_name='MemTurn.entities', index=2,
      number=3, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='lifespan', full_name='MemTurn.lifespan', index=3,
      number=4, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='user', full_name='MemTurn.user', index=4,
      number=5, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='entitySet', full_name='MemTurn.entitySet', index=5,
      number=6, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[_MEMTURN_ENTITYSETENTRY, ],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=231,
  serialized_end=453,
)

_MEMTURN_ENTITYSETENTRY.containing_type = _MEMTURN
_MEMTURN.fields_by_name['intent'].message_type = _MEMINTENT
_MEMTURN.fields_by_name['entities'].message_type = _MEMENTITY
_MEMTURN.fields_by_name['user'].message_type = _MEMUSERINFO
_MEMTURN.fields_by_name['entitySet'].message_type = _MEMTURN_ENTITYSETENTRY
DESCRIPTOR.message_types_by_name['MemMessage'] = _MEMMESSAGE
DESCRIPTOR.message_types_by_name['MemUserInfo'] = _MEMUSERINFO
DESCRIPTOR.message_types_by_name['MemIntent'] = _MEMINTENT
DESCRIPTOR.message_types_by_name['MemEntity'] = _MEMENTITY
DESCRIPTOR.message_types_by_name['MemTurn'] = _MEMTURN
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

MemMessage = _reflection.GeneratedProtocolMessageType('MemMessage', (_message.Message,), {
  'DESCRIPTOR' : _MEMMESSAGE,
  '__module__' : 'memory_pb2'
  # @@protoc_insertion_point(class_scope:MemMessage)
  })
_sym_db.RegisterMessage(MemMessage)

MemUserInfo = _reflection.GeneratedProtocolMessageType('MemUserInfo', (_message.Message,), {
  'DESCRIPTOR' : _MEMUSERINFO,
  '__module__' : 'memory_pb2'
  # @@protoc_insertion_point(class_scope:MemUserInfo)
  })
_sym_db.RegisterMessage(MemUserInfo)

MemIntent = _reflection.GeneratedProtocolMessageType('MemIntent', (_message.Message,), {
  'DESCRIPTOR' : _MEMINTENT,
  '__module__' : 'memory_pb2'
  # @@protoc_insertion_point(class_scope:MemIntent)
  })
_sym_db.RegisterMessage(MemIntent)

MemEntity = _reflection.GeneratedProtocolMessageType('MemEntity', (_message.Message,), {
  'DESCRIPTOR' : _MEMENTITY,
  '__module__' : 'memory_pb2'
  # @@protoc_insertion_point(class_scope:MemEntity)
  })
_sym_db.RegisterMessage(MemEntity)

MemTurn = _reflection.GeneratedProtocolMessageType('MemTurn', (_message.Message,), {

  'EntitySetEntry' : _reflection.GeneratedProtocolMessageType('EntitySetEntry', (_message.Message,), {
    'DESCRIPTOR' : _MEMTURN_ENTITYSETENTRY,
    '__module__' : 'memory_pb2'
    # @@protoc_insertion_point(class_scope:MemTurn.EntitySetEntry)
    })
  ,
  'DESCRIPTOR' : _MEMTURN,
  '__module__' : 'memory_pb2'
  # @@protoc_insertion_point(class_scope:MemTurn)
  })
_sym_db.RegisterMessage(MemTurn)
_sym_db.RegisterMessage(MemTurn.EntitySetEntry)


DESCRIPTOR._options = None
_MEMTURN_ENTITYSETENTRY._options = None

_SDSMEMORY = _descriptor.ServiceDescriptor(
  name='SdsMemory',
  full_name='SdsMemory',
  file=DESCRIPTOR,
  index=0,
  serialized_options=None,
  serialized_start=456,
  serialized_end=834,
  methods=[
  _descriptor.MethodDescriptor(
    name='UserInit',
    full_name='SdsMemory.UserInit',
    index=0,
    containing_service=None,
    input_type=_MEMUSERINFO,
    output_type=_MEMMESSAGE,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='UserEnd',
    full_name='SdsMemory.UserEnd',
    index=1,
    containing_service=None,
    input_type=_MEMUSERINFO,
    output_type=_MEMMESSAGE,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='PassTurn',
    full_name='SdsMemory.PassTurn',
    index=2,
    containing_service=None,
    input_type=_MEMUSERINFO,
    output_type=_MEMMESSAGE,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='AddMemory',
    full_name='SdsMemory.AddMemory',
    index=3,
    containing_service=None,
    input_type=_MEMTURN,
    output_type=_MEMMESSAGE,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='GetLastMemory',
    full_name='SdsMemory.GetLastMemory',
    index=4,
    containing_service=None,
    input_type=_MEMUSERINFO,
    output_type=_MEMTURN,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='GetAllMemory',
    full_name='SdsMemory.GetAllMemory',
    index=5,
    containing_service=None,
    input_type=_MEMUSERINFO,
    output_type=_MEMTURN,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='GetMemoryByIntent',
    full_name='SdsMemory.GetMemoryByIntent',
    index=6,
    containing_service=None,
    input_type=_MEMINTENT,
    output_type=_MEMTURN,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='GetMemoryByEntity',
    full_name='SdsMemory.GetMemoryByEntity',
    index=7,
    containing_service=None,
    input_type=_MEMENTITY,
    output_type=_MEMTURN,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='ClearMemory',
    full_name='SdsMemory.ClearMemory',
    index=8,
    containing_service=None,
    input_type=_MEMUSERINFO,
    output_type=_MEMMESSAGE,
    serialized_options=None,
  ),
])
_sym_db.RegisterServiceDescriptor(_SDSMEMORY)

DESCRIPTOR.services_by_name['SdsMemory'] = _SDSMEMORY

# @@protoc_insertion_point(module_scope)
